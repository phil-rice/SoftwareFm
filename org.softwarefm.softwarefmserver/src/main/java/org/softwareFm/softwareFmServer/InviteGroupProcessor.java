/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.softwareFmServer;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.common.IGroupsReader;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.eclipse.user.IUserMembershipReader;
import org.softwareFm.server.processors.IMailer;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.server.processors.ISignUpChecker;

public class InviteGroupProcessor extends AbstractAddToGroupProcessor {

	private final IFunction1<Map<String, Object>, String> userCryptoFn;
	private final IUserMembershipReader userMembershipReader;
	private final IGroupsReader groupsReader;

	public InviteGroupProcessor(ITakeOnProcessor takeOnProcessor, ISignUpChecker signUpChecker, IFunction1<String, String> emailToSfmId, Callable<String> saltGenerator, Callable<String> softwareFmIdGenerator, IMailer mailer, IFunction1<Map<String, Object>, String> userCryptoFn, IUserMembershipReader userMembershipReader, IGroupsReader groupsReader) {
		super(null, CommonConstants.POST, GroupConstants.inviteCommandPrefix, takeOnProcessor, signUpChecker, emailToSfmId, saltGenerator, softwareFmIdGenerator, mailer);
		this.userCryptoFn = userCryptoFn;
		this.userMembershipReader = userMembershipReader;
		this.groupsReader = groupsReader;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		checkForParameter(parameters, LoginConstants.softwareFmIdKey, GroupConstants.groupIdKey, GroupConstants.takeOnFromKey, GroupConstants.takeOnEmailPattern, GroupConstants.takeOnEmailListKey);
		String groupId = (String) parameters.get(GroupConstants.groupIdKey);
		String softwareFmId = (String) parameters.get(LoginConstants.softwareFmIdKey);
		String from = (String) parameters.get(GroupConstants.takeOnFromKey);// this dude is now the admin
		String userCrypto = Functions.call(userCryptoFn, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
		String groupCrypto = IUserMembershipReader.Utils.findGroupCrytpo(userMembershipReader, softwareFmId, userCrypto, groupId);
		String status = userMembershipReader.getMembershipProperty(softwareFmId, userCrypto, groupId, GroupConstants.membershipStatusKey); // could optimise and merge this with last
		String groupName =groupsReader.getGroupProperty(groupId, groupCrypto, GroupConstants.groupNameKey);
		if (!GroupConstants.adminStatus.equals(status))
			throw new IllegalArgumentException(MessageFormat.format(GroupConstants.cannotInviteToGroupUnlessAdmin, groupName, status));
		if (groupName == null)
			throw new IllegalStateException(MessageFormat.format(GroupConstants.groupNameIsNull, groupId));
		if (!Strings.isEmail(from))
			throw new IllegalArgumentException(MessageFormat.format(GroupConstants.invalidEmail, from));
		String expectedSfmId = Functions.call(emailToSfmId, from);
		if (!softwareFmId.equals(expectedSfmId))
			throw new IllegalArgumentException(MessageFormat.format(GroupConstants.emailSfmMismatch, from, expectedSfmId, softwareFmId));

		List<String> memberList = getEmailList(parameters);
		addUsersToGroup(groupId, groupCrypto, memberList);

		sendInvitationEmails(parameters, groupName, memberList);
		return IProcessResult.Utils.processString("");
	}
}