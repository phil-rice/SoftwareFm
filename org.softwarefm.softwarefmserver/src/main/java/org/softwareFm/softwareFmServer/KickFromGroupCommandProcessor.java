package org.softwareFm.softwareFmServer;

import java.text.MessageFormat;
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

public class KickFromGroupCommandProcessor extends AbstractCommandProcessor{
	private final IGroups groups;
	private final IFunction1<Map<String, Object>, String> userCryptoFn;
	private final IUserMembership userMembership;

	public KickFromGroupCommandProcessor(IGroups groups, IUserMembership userMembership, IFunction1<Map<String, Object>, String> userCryptoFn) {
		super(null, CommonConstants.POST, GroupConstants.kickFromGroupPrefix);
		this.groups = groups;
		this.userMembership = userMembership;
		this.userCryptoFn = userCryptoFn;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		checkForParameter(parameters, LoginConstants.softwareFmIdKey, GroupConstants.groupIdKey,  GroupConstants.objectSoftwareFmId);
		String softwareFmId = (String) parameters.get(LoginConstants.softwareFmIdKey);
		String objectSoftwareFmId = (String) parameters.get(GroupConstants.objectSoftwareFmId);
		String groupId = (String) parameters.get(GroupConstants.groupIdKey);
		String userCrypto = Functions.call(userCryptoFn, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
		String objectCrypto = Functions.call(userCryptoFn, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, objectSoftwareFmId));
		String groupCrypto = IUserMembershipReader.Utils.findGroupCrytpo(userMembership, softwareFmId, userCrypto, groupId);
		String myStatus = userMembership.getMembershipProperty(softwareFmId, userCrypto, groupId, GroupConstants.membershipStatusKey);
		if (!GroupConstants.adminStatus.equals(myStatus))
			throw new IllegalArgumentException(MessageFormat.format(GroupConstants.cannotKickUnlessAdmin, groupId, softwareFmId, myStatus, objectSoftwareFmId));
		userMembership.remove(objectSoftwareFmId, objectCrypto, groupId, groupCrypto);
		groups.removeUser(groupId, groupCrypto, objectSoftwareFmId);
		return IProcessResult.Utils.processString("");
	}
}
