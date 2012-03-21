package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.util.Map;

import org.softwareFm.crowdsource.api.ICrowdSourceReadWriteApi;
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
	private final ICrowdSourceReadWriteApi readWriteApi;

	public AcceptInviteGroupProcessor(IUserCryptoAccess userCryptoAccess, ICrowdSourceReadWriteApi readWriteApi) {
		super(CommonConstants.POST, GroupConstants.acceptInvitePrefix);
		this.userCryptoAccess = userCryptoAccess;
		this.readWriteApi = readWriteApi;
	}

	@Override
	protected IProcessResult execute(String actualUrl, final Map<String, Object> parameters) {
		checkForParameter(parameters, LoginConstants.softwareFmIdKey, GroupConstants.groupIdKey, GroupConstants.membershipStatusKey);
		readWriteApi.modifyUserMembership(new ICallback2<IGroups, IUserMembership>(){
			@Override
			public void process(IGroups groups, IUserMembership userMembership) throws Exception {
				String softwareFmId = (String) parameters.get(LoginConstants.softwareFmIdKey);
				String groupId = (String) parameters.get(GroupConstants.groupIdKey);
				final String newStatus = (String) parameters.get(GroupConstants.membershipStatusKey);
				String userCrypto = userCryptoAccess.getCryptoForUser(softwareFmId);
				String groupCrypto = IUserMembershipReader.Utils.findGroupCrytpo(userMembership, softwareFmId, userCrypto, groupId);
				groups.setUserProperty(groupId, groupCrypto, softwareFmId, GroupConstants.membershipStatusKey, newStatus);
				userMembership.setMembershipProperty(softwareFmId, userCrypto, groupId, GroupConstants.membershipStatusKey, newStatus);
			}});
		return IProcessResult.Utils.processString("");
	}
}
