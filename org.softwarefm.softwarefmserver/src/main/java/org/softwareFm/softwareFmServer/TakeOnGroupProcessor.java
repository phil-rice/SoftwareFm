/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.softwareFmServer;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.server.processors.IMailer;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.server.processors.ISignUpChecker;
import org.softwareFm.server.processors.SignUpResult;
import org.softwareFm.server.processors.internal.AbstractCommandProcessor;

public class TakeOnGroupProcessor extends AbstractCommandProcessor {

	private final ITakeOnProcessor takeOnProcessor;
	private final Callable<String> cryptoGenerator;
	private final IFunction1<String, String> emailToSfmId;
	private final ISignUpChecker signUpChecker;
	private final Callable<String> saltGenerator;
	private final Callable<String> softwareFmIdGenerator;
	private final IMailer mailer;

	public TakeOnGroupProcessor(ITakeOnProcessor takeOnProcessor, ISignUpChecker signUpChecker, Callable<String> cryptoGenerator, IFunction1<String, String> emailToSfmId, Callable<String> saltGenerator, Callable<String> softwareFmIdGenerator, IMailer mailer) {
		super(null, CommonConstants.POST, GroupConstants.takeOnCommandPrefix);
		this.takeOnProcessor = takeOnProcessor;
		this.signUpChecker = signUpChecker;
		this.cryptoGenerator = cryptoGenerator;
		this.emailToSfmId = emailToSfmId;
		this.saltGenerator = saltGenerator;
		this.softwareFmIdGenerator = softwareFmIdGenerator;
		this.mailer = mailer;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		checkForParameter(parameters, LoginConstants.softwareFmIdKey, GroupConstants.groupNameKey, GroupConstants.takeOnEmailPattern, GroupConstants.takeOnFromKey, GroupConstants.takeOnSubjectKey, GroupConstants.takeOnEmailListKey);
		String softwareFmId = (String) parameters.get(LoginConstants.softwareFmIdKey);
		String groupName = (String) parameters.get(GroupConstants.groupNameKey);
		String emailPattern = (String) parameters.get(GroupConstants.takeOnEmailPattern);
		String memberListRaw = (String) parameters.get(GroupConstants.takeOnEmailListKey);
		String rawSubject = (String) parameters.get(GroupConstants.takeOnSubjectKey);
		String from = (String) parameters.get(GroupConstants.takeOnFromKey);// this dude is now the admin

		String groupCrypto = Callables.call(cryptoGenerator);
		List<String> memberList = Strings.splitIgnoreBlanks(memberListRaw, ",");
		if (!Strings.isEmail(from))
			throw new IllegalArgumentException(MessageFormat.format(GroupConstants.invalidEmail, from));
		String expectedSfmId = Functions.call(emailToSfmId, from);
		if (!softwareFmId.equals(expectedSfmId))
			throw new IllegalArgumentException(MessageFormat.format(GroupConstants.emailSfmMismatch, from, expectedSfmId, softwareFmId));
		for (String email : memberList)
			if (!Strings.isEmail(email))
				throw new IllegalArgumentException(MessageFormat.format(GroupConstants.invalidEmail, from));
		String groupId = takeOnProcessor.createGroup(groupName, groupCrypto);
		takeOnProcessor.addExistingUserToGroup(groupId, groupName, groupCrypto, softwareFmId, from, GroupConstants.adminStatus);
		for (String email : memberList) {
			String newSoftwareFmId = Functions.call(emailToSfmId, email);
			if (newSoftwareFmId == null) {
				newSoftwareFmId = Callables.call(softwareFmIdGenerator);
				String moniker = Strings.split(email, '@').pre;
				String salt = Callables.call(saltGenerator);
				SignUpResult signUpResult = signUpChecker.signUp(email, moniker, salt, "not set", newSoftwareFmId);
				if (signUpResult.errorMessage != null)
					throw new RuntimeException(signUpResult.errorMessage);
			}
			takeOnProcessor.addExistingUserToGroup(groupId, groupName, groupCrypto, newSoftwareFmId, email, GroupConstants.invitedStatus);

		}
		for (String email : memberList) {
			String subject = replaceMarkers(rawSubject, groupName, email);
			String message = replaceMarkers(emailPattern, groupName, email);
			mailer.mail(from, email, subject, message);
		}
		return IProcessResult.Utils.processString("");
	}

	protected String replaceMarkers(String rawMessage, String groupName, String email) {
		return rawMessage.replace(GroupConstants.emailMarker, email).replace(GroupConstants.groupNameMarker, groupName);
	}
}