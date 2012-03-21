/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.softwareFm.crowdsource.api.ICrowdSourceReadWriteApi;
import org.softwareFm.crowdsource.api.ICryptoGenerators;
import org.softwareFm.crowdsource.api.IIdAndSaltGenerator;
import org.softwareFm.crowdsource.api.IUserCryptoAccess;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.api.server.IServerDoers;
import org.softwareFm.crowdsource.api.server.ITakeOnProcessor;
import org.softwareFm.crowdsource.api.user.IGroups;
import org.softwareFm.crowdsource.api.user.IUserMembership;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback2;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.strings.Strings;

public class TakeOnGroupProcessor extends AbstractAddToGroupProcessor {

	private final ICrowdSourceReadWriteApi readWriteApi;
	private final ICryptoGenerators cryptoGenerators;

	public TakeOnGroupProcessor( IServerDoers serverDoers, IUserCryptoAccess userCryptoAccess, IIdAndSaltGenerator idAndSaltGenerator, ICryptoGenerators cryptoGenerators, ICrowdSourceReadWriteApi readWriteApi) {
		super(CommonConstants.POST, GroupConstants.takeOnCommandPrefix, serverDoers, userCryptoAccess, idAndSaltGenerator);
		this.cryptoGenerators = cryptoGenerators;
		this.readWriteApi = readWriteApi;

	}

	@Override
	protected IProcessResult execute(String actualUrl, final Map<String, Object> parameters) {
		checkForParameter(parameters, LoginConstants.softwareFmIdKey, GroupConstants.groupNameKey, GroupConstants.takeOnEmailPattern, GroupConstants.takeOnFromKey, GroupConstants.takeOnSubjectKey, GroupConstants.takeOnEmailListKey);
		final AtomicReference<String> replyMessage = new AtomicReference<String>();
		readWriteApi.modifyUserMembership(new ICallback2<IGroups, IUserMembership>() {
			@Override
			public void process(IGroups first, IUserMembership second) throws Exception {
				String groupName = (String) parameters.get(GroupConstants.groupNameKey);
				String softwareFmId = (String) parameters.get(LoginConstants.softwareFmIdKey);
				String fromEmail = (String) parameters.get(GroupConstants.takeOnFromKey);// this dude is now the admin

				String groupCrypto = cryptoGenerators.groupCrypto();
				if (!Strings.isEmail(fromEmail))
					throw new IllegalArgumentException(MessageFormat.format(GroupConstants.invalidEmail, fromEmail));
				String expectedSfmId = userCryptoAccess.emailToSoftwareFnId(fromEmail);
				if (!softwareFmId.equals(expectedSfmId))
					throw new IllegalArgumentException(MessageFormat.format(GroupConstants.emailSfmMismatch, fromEmail, expectedSfmId, softwareFmId));

				ITakeOnProcessor takeOnProcessor = serverDoers.getTakeOnProcessor();
				String groupId = takeOnProcessor.createGroup(groupName, groupCrypto);
				takeOnProcessor.addExistingUserToGroup(groupId, groupCrypto, softwareFmId, fromEmail, GroupConstants.adminStatus);

				List<String> memberList = getEmailList(parameters);
				addUsersToGroup(groupId, groupCrypto, memberList);

				sendInvitationEmails(parameters, groupName, memberList);
				replyMessage.set(groupId);
			}
		});
		return IProcessResult.Utils.processString(replyMessage.get());
	}
}