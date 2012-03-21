package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.text.MessageFormat;
import java.util.Arrays;
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

public class LeaveGroupCommandProcessor extends AbstractCallProcessor {

	private final ICrowdSourceReadWriteApi readWriteApi;
	private final IUserCryptoAccess userCryptoAccess;

	public LeaveGroupCommandProcessor(ICrowdSourceReadWriteApi readWriteApi, IUserCryptoAccess userCryptoAccess) {
		super(CommonConstants.POST, GroupConstants.leaveGroupPrefix);
		this.readWriteApi = readWriteApi;
		this.userCryptoAccess = userCryptoAccess;
	}

	@Override
	protected IProcessResult execute(String actualUrl, final Map<String, Object> parameters) {
		checkForParameter(parameters, LoginConstants.softwareFmIdKey, GroupConstants.groupIdKey);
		readWriteApi.modifyUserMembership(new ICallback2<IGroups, IUserMembership>() {
			@Override
			public void process(IGroups groups, IUserMembership userMembership) throws Exception {
				String softwareFmId = (String) parameters.get(LoginConstants.softwareFmIdKey);
				String groupId = (String) parameters.get(GroupConstants.groupIdKey);
				String userCrypto = userCryptoAccess.getCryptoForUser(softwareFmId);
				String groupCrypto = IUserMembershipReader.Utils.findGroupCrytpo( userMembership, softwareFmId, userCrypto, groupId);
				checkIfAdminGroupIsEmpty(groups, userMembership, softwareFmId, userCrypto, groupId, groupCrypto);
				userMembership.remove(softwareFmId, userCrypto, groupId, groupCrypto);
				groups.removeUsers(groupId, groupCrypto, Arrays.asList(softwareFmId));
			}
		});
		return IProcessResult.Utils.processString("");
	}

	private void checkIfAdminGroupIsEmpty(IGroups groups, IUserMembership userMembership, String softwareFmId, String userCrypto, String groupId, String groupCrypto) {
		String mainStatus = userMembership.getMembershipProperty(softwareFmId, userCrypto, groupId, GroupConstants.membershipStatusKey);
		if (!GroupConstants.adminStatus.equals(mainStatus))
			return;
		int membershipCount = groups.membershipCount(groupId, groupCrypto);
		if (membershipCount > 1)
			throw new IllegalArgumentException(MessageFormat.format(GroupConstants.cannotLeaveIfAdminAndGroupIsntEmpty, groupId, softwareFmId, membershipCount));

	}

}
