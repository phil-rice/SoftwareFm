package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.util.Map;

import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
import org.softwareFm.crowdsource.api.IUserCryptoAccess;
import org.softwareFm.crowdsource.api.server.AbstractCallProcessor;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.api.user.IGroups;
import org.softwareFm.crowdsource.api.user.IUserMembership;
import org.softwareFm.crowdsource.api.user.IUserMembershipReader;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback2;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;

public class AcceptInviteGroupProcessor extends AbstractCallProcessor {

	private final IUserCryptoAccess userCryptoAccess;
	private final IUserAndGroupsContainer container;

	public AcceptInviteGroupProcessor(IUserCryptoAccess userCryptoAccess, IUserAndGroupsContainer container) {
		super(CommonConstants.POST, GroupConstants.acceptInvitePrefix);
		this.userCryptoAccess = userCryptoAccess;
		this.container = container;
	}

	@Override
	protected IProcessResult execute(String actualUrl, final Map<String, Object> parameters) {
		checkForParameter(parameters, LoginConstants.softwareFmIdKey, GroupConstants.groupIdKey);
		container.accessUserMembership(new ICallback2<IGroups, IUserMembership>(){
			@Override
			public void process(IGroups groups, IUserMembership userMembership) throws Exception {
				String softwareFmId = (String) parameters.get(LoginConstants.softwareFmIdKey);
				String groupId = (String) parameters.get(GroupConstants.groupIdKey);
				final String newStatus = GroupConstants.memberStatus;
				String userCrypto = userCryptoAccess.getCryptoForUser(softwareFmId);
				String groupCrypto = IUserMembershipReader.Utils.findGroupCrytpo(userMembership, softwareFmId, userCrypto, groupId);
				groups.setUserProperty(groupId, groupCrypto, softwareFmId, GroupConstants.membershipStatusKey, newStatus);
				userMembership.setMembershipProperty(softwareFmId, userCrypto, groupId, GroupConstants.membershipStatusKey, newStatus);
			}}).get();
		return IProcessResult.Utils.processString("");
	}
}
