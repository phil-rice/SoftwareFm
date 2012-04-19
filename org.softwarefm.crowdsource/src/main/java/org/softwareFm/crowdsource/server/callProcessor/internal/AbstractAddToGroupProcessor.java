/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.api.IIdAndSaltGenerator;
import org.softwareFm.crowdsource.api.IUserCryptoAccess;
import org.softwareFm.crowdsource.api.server.AbstractCallProcessor;
import org.softwareFm.crowdsource.api.server.IServerDoers;
import org.softwareFm.crowdsource.api.server.SignUpResult;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.strings.Strings;

public abstract class AbstractAddToGroupProcessor extends AbstractCallProcessor {

	protected final IServerDoers serverDoers;
	protected final IUserCryptoAccess userCryptoAccess;
	protected final IIdAndSaltGenerator idAndSaltGenerator;

	public AbstractAddToGroupProcessor(String method, String prefix, IServerDoers serverDoers, IUserCryptoAccess userCryptoAccess, IIdAndSaltGenerator idAndSaltGenerator) {
		super(method, prefix);
		this.serverDoers = serverDoers;
		this.userCryptoAccess = userCryptoAccess;
		this.idAndSaltGenerator = idAndSaltGenerator;
	}

	protected void sendInvitationEmails(Map<String, Object> parameters, String groupName, List<String> memberList) {
		String from = (String) parameters.get(GroupConstants.takeOnFromKey);
		String emailPattern = (String) parameters.get(GroupConstants.takeOnEmailPattern);
		String rawSubject = (String) parameters.get(GroupConstants.takeOnSubjectKey);
		for (String email : memberList) {
			String subject = replaceMarkers(rawSubject, groupName, from);
			String message = replaceMarkers(emailPattern, groupName, from);
			serverDoers.getMailer().mail(from, email, subject, message);
		}
	}

	protected void addUsersToGroup(String groupId, String groupCrypto, List<String> memberList) {
		if (groupCrypto == null)
			throw new NullPointerException();
		for (String email : memberList) {
			String newSoftwareFmId = userCryptoAccess.emailToSoftwareFmId(email);
			if (newSoftwareFmId == null) {
				newSoftwareFmId = idAndSaltGenerator.makeNewUserId();
				String moniker = Strings.split(email, '@').pre;
				String salt = idAndSaltGenerator.makeSalt();
				SignUpResult signUpResult = serverDoers.getSignUpChecker().signUp(email, moniker, salt, "not set", newSoftwareFmId);
				if (signUpResult.errorMessage != null)
					throw new RuntimeException(signUpResult.errorMessage);
			}
			serverDoers.getTakeOnProcessor().addExistingUserToGroup(groupId, groupCrypto, newSoftwareFmId, email, GroupConstants.invitedStatus);
		}
	}

	protected List<String> getEmailList(Map<String, Object> parameters) {
		String memberListRaw = (String) parameters.get(GroupConstants.takeOnEmailListKey);
		List<String> memberList = Strings.splitIgnoreBlanks(memberListRaw, ",");
		for (String email : memberList)
			if (!Strings.isEmail(email))
				throw new IllegalArgumentException(MessageFormat.format(GroupConstants.invalidEmail, email));
		return memberList;
	}

	protected String replaceMarkers(String rawMessage, String groupName, String email) {
		return rawMessage.replace(GroupConstants.emailMarker, email).replace(GroupConstants.groupNameMarker, groupName);
	}
}