package org.softwareFm.softwareFmServer;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
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

public class LeaveGroupCommandProcessor extends AbstractCommandProcessor {
	private final IGroups groups;
	private final IFunction1<Map<String, Object>, String> userCryptoFn;
	private final IUserMembership userMembership;

	public LeaveGroupCommandProcessor(IGroups groups, IUserMembership userMembership, IFunction1<Map<String, Object>, String> userCryptoFn) {
		super(null, CommonConstants.POST, GroupConstants.leaveGroupPrefix);
		this.groups = groups;
		this.userMembership = userMembership;
		this.userCryptoFn = userCryptoFn;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		checkForParameter(parameters, LoginConstants.softwareFmIdKey, GroupConstants.groupIdKey);
		String softwareFmId = (String) parameters.get(LoginConstants.softwareFmIdKey);
		String groupId = (String) parameters.get(GroupConstants.groupIdKey);
		String userCrypto = Functions.call(userCryptoFn, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
		String groupCrypto = IUserMembershipReader.Utils.findGroupCrytpo(userMembership, softwareFmId, userCrypto, groupId);
		checkIfAdminGroupIsEmpty(softwareFmId, userCrypto, groupId, groupCrypto);
		userMembership.remove(softwareFmId, userCrypto, groupId, groupCrypto);
		groups.removeUsers(groupId, groupCrypto, Arrays.asList(softwareFmId));
		return IProcessResult.Utils.processString("");
	}

	private void checkIfAdminGroupIsEmpty(String softwareFmId, String userCrypto, String groupId, String groupCrypto) {
		String mainStatus = userMembership.getMembershipProperty(softwareFmId, userCrypto, groupId, GroupConstants.membershipStatusKey);
		if (!GroupConstants.adminStatus.equals(mainStatus))
			return;
		int membershipCount = groups.membershipCount(groupId, groupCrypto);
		if (membershipCount > 1)
			throw new IllegalArgumentException(MessageFormat.format(GroupConstants.cannotLeaveIfAdminAndGroupIsntEmpty, groupId, softwareFmId, membershipCount));

	}

	private void checkMainUserIsAdminAndNoOthersAreAdmin(String softwareFmId, String userCrypto, List<String> objectSoftwareFmIds, String groupId) {
		String mainStatus = userMembership.getMembershipProperty(softwareFmId, userCrypto, groupId, GroupConstants.membershipStatusKey);
		if (!GroupConstants.adminStatus.equals(mainStatus))
			throw new IllegalArgumentException(MessageFormat.format(GroupConstants.cannotKickUnlessAdmin, groupId, softwareFmId, mainStatus, objectSoftwareFmIds));
		for (String objectSoftwareFmId : objectSoftwareFmIds) {
			String objectCrypto = Functions.call(userCryptoFn, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, objectSoftwareFmId));
			String objectsStatus = userMembership.getMembershipProperty(objectSoftwareFmId, objectCrypto, groupId, GroupConstants.membershipStatusKey);
			if (GroupConstants.adminStatus.equals(objectsStatus))
				throw new IllegalArgumentException(MessageFormat.format(GroupConstants.cannotKickAdmin, groupId, softwareFmId, mainStatus, objectSoftwareFmId));
		}
	}
}
