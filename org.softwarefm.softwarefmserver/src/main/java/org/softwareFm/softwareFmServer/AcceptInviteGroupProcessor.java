package org.softwareFm.softwareFmServer;

import java.util.Map;

import org.softwareFm.common.IGroups;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.eclipse.user.IUserMembership;
import org.softwareFm.eclipse.user.IUserMembershipReader;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.server.processors.internal.AbstractCommandProcessor;

public class AcceptInviteGroupProcessor extends AbstractCommandProcessor {

	private final IGroups groups;
	private final IFunction1<Map<String, Object>, String> userCryptoFn;
	private final IUserMembership userMembership;

	public AcceptInviteGroupProcessor(IGroups groups, IUserMembership userMembership, IFunction1<Map<String, Object>, String> userCryptoFn) {
		super(null, CommonConstants.POST, GroupConstants.acceptInvitePrefix);
		this.groups = groups;
		this.userMembership = userMembership;
		this.userCryptoFn = userCryptoFn;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		checkForParameter(parameters, LoginConstants.softwareFmIdKey, GroupConstants.groupIdKey, GroupConstants.membershipStatusKey);
		String softwareFmId = (String) parameters.get(LoginConstants.softwareFmIdKey);
		String groupId = (String) parameters.get(GroupConstants.groupIdKey);
		final String newStatus = (String) parameters.get(GroupConstants.membershipStatusKey);
		String userCrypto = Functions.call(userCryptoFn, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
		String groupCrypto = IUserMembershipReader.Utils.findGroupCrytpo(userMembership, softwareFmId, userCrypto, groupId);
		groups.setUserProperty(groupId, groupCrypto, softwareFmId, GroupConstants.membershipStatusKey, newStatus);
		userMembership.setMembershipProperty(softwareFmId, userCrypto, groupId, GroupConstants.membershipStatusKey, newStatus);
		return IProcessResult.Utils.processString("");
	}
}
