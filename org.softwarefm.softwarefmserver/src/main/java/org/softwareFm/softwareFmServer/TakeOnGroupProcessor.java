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

public class TakeOnGroupProcessor extends AbstractAddToGroupProcessor {


	private final Callable<String> groupCryptoGenerator;

	public TakeOnGroupProcessor(ITakeOnProcessor takeOnProcessor, ISignUpChecker signUpChecker, Callable<String> groupCryptoGenerator, IFunction1<String, String> emailToSfmId, Callable<String> saltGenerator, Callable<String> softwareFmIdGenerator, IMailer mailer) {
		super(null, CommonConstants.POST, GroupConstants.takeOnCommandPrefix, takeOnProcessor, signUpChecker, emailToSfmId, saltGenerator, softwareFmIdGenerator, mailer);
		this.groupCryptoGenerator = groupCryptoGenerator;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		checkForParameter(parameters, LoginConstants.softwareFmIdKey, GroupConstants.groupNameKey, GroupConstants.takeOnEmailPattern, GroupConstants.takeOnFromKey, GroupConstants.takeOnSubjectKey, GroupConstants.takeOnEmailListKey);
		String groupName = (String) parameters.get(GroupConstants.groupNameKey);
		String softwareFmId = (String) parameters.get(LoginConstants.softwareFmIdKey);
		String from = (String) parameters.get(GroupConstants.takeOnFromKey);// this dude is now the admin

		String groupCrypto = Callables.call(groupCryptoGenerator);
		if (!Strings.isEmail(from))
			throw new IllegalArgumentException(MessageFormat.format(GroupConstants.invalidEmail, from));
		String expectedSfmId = Functions.call(emailToSfmId, from);
		if (!softwareFmId.equals(expectedSfmId))
			throw new IllegalArgumentException(MessageFormat.format(GroupConstants.emailSfmMismatch, from, expectedSfmId, softwareFmId));
		
		String groupId = takeOnProcessor.createGroup(groupName, groupCrypto);
		takeOnProcessor.addExistingUserToGroup(groupId, groupCrypto, softwareFmId, from, GroupConstants.adminStatus);

		List<String> memberList = getEmailList(parameters);
		addUsersToGroup(groupId, groupCrypto, memberList);

		sendInvitationEmails(parameters, groupName, memberList);
		return IProcessResult.Utils.processString(groupId);
	}
}