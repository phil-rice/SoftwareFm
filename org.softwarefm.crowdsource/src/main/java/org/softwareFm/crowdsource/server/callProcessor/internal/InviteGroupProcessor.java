/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.api.ICrowdSourcedReadWriteApi;
import org.softwareFm.crowdsource.api.IIdAndSaltGenerator;
import org.softwareFm.crowdsource.api.IUserCryptoAccess;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.api.server.IServerDoers;
import org.softwareFm.crowdsource.api.user.IGroups;
import org.softwareFm.crowdsource.api.user.IUserMembership;
import org.softwareFm.crowdsource.api.user.IUserMembershipReader;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback2;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.strings.Strings;

public class InviteGroupProcessor extends AbstractAddToGroupProcessor {

	private final ICrowdSourcedReadWriteApi crowdSourcedReadWriteApi;

	public InviteGroupProcessor(ICrowdSourcedReadWriteApi crowdSourcedReadWriteApi, IServerDoers serverDoers, IUserCryptoAccess userCryptoAccess, IIdAndSaltGenerator idAndSaltGenerator) {
		super(CommonConstants.POST, GroupConstants.inviteCommandPrefix, serverDoers, userCryptoAccess, idAndSaltGenerator);
		this.crowdSourcedReadWriteApi = crowdSourcedReadWriteApi;
	}

	@Override
	protected IProcessResult execute(String actualUrl, final Map<String, Object> parameters) {
		checkForParameter(parameters, LoginConstants.softwareFmIdKey, GroupConstants.groupIdKey, GroupConstants.takeOnFromKey, GroupConstants.takeOnEmailPattern, GroupConstants.takeOnEmailListKey);
		crowdSourcedReadWriteApi.modifyUserMembership(new ICallback2<IGroups, IUserMembership>(){
			@Override
			public void process(IGroups groups, IUserMembership userMembership) throws Exception {
				String groupId = (String) parameters.get(GroupConstants.groupIdKey);
				final String softwareFmId = (String) parameters.get(LoginConstants.softwareFmIdKey);
				String fromEmail = (String) parameters.get(GroupConstants.takeOnFromKey);// this dude is now the admin
				String userCrypto = userCryptoAccess.getCryptoForUser(softwareFmId);
				String groupCrypto = IUserMembershipReader.Utils.findGroupCrytpo(userMembership, softwareFmId, userCrypto, groupId);
				String status = userMembership.getMembershipProperty(softwareFmId, userCrypto, groupId, GroupConstants.membershipStatusKey); // could optimise and merge this with last
				String groupName = groups.getGroupProperty(groupId, groupCrypto, GroupConstants.groupNameKey);
				if (!GroupConstants.adminStatus.equals(status))
					throw new IllegalArgumentException(MessageFormat.format(GroupConstants.cannotInviteToGroupUnlessAdmin, groupName, status));
				if (groupName == null)
					throw new IllegalStateException(MessageFormat.format(GroupConstants.groupNameIsNull, groupId));
				if (!Strings.isEmail(fromEmail))
					throw new IllegalArgumentException(MessageFormat.format(GroupConstants.invalidEmail, fromEmail));
				
				String expectedSfmId = userCryptoAccess.emailToSoftwareFmId(fromEmail);
				if (!softwareFmId.equals(expectedSfmId))
					throw new IllegalArgumentException(MessageFormat.format(GroupConstants.emailSfmMismatch, fromEmail, expectedSfmId, softwareFmId));
				
				List<String> memberList = getEmailList(parameters);
				addUsersToGroup(groupId, groupCrypto, memberList);
				
				sendInvitationEmails(parameters, groupName, memberList);
				
			}});
		
		return IProcessResult.Utils.processString("");
	}
}