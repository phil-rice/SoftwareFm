package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.text.MessageFormat;
import java.util.List;
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
import org.softwareFm.crowdsource.utilities.strings.Strings;

public class KickFromGroupCommandProcessor extends AbstractCallProcessor {
	private final IUserCryptoAccess cryptoAccess;
	private final ICrowdSourceReadWriteApi api;

	public KickFromGroupCommandProcessor(ICrowdSourceReadWriteApi api, IUserCryptoAccess cryptoAccess) {
		super(CommonConstants.POST, GroupConstants.kickFromGroupPrefix);
		this.api = api;
		this.cryptoAccess = cryptoAccess;
	}

	@Override
	protected IProcessResult execute(String actualUrl, final Map<String, Object> parameters) {
		checkForParameter(parameters, LoginConstants.softwareFmIdKey, GroupConstants.groupIdKey, GroupConstants.objectSoftwareFmId);
		api.modifyUserMembership(new ICallback2<IGroups, IUserMembership>(){
			@Override
			public void process(IGroups groups, IUserMembership userMembership) throws Exception {
				String softwareFmId = (String) parameters.get(LoginConstants.softwareFmIdKey);
				List<String> objectSoftwareFmIds = Strings.splitIgnoreBlanks((String) parameters.get(GroupConstants.objectSoftwareFmId), ",");
				String groupId = (String) parameters.get(GroupConstants.groupIdKey);
				String userCrypto = cryptoAccess.getCryptoForUser( softwareFmId);
				String groupCrypto = IUserMembershipReader.Utils.findGroupCrytpo(userMembership, softwareFmId, userCrypto, groupId);
				checkMainUserIsAdminAndNoOthersAreAdmin(userMembership, softwareFmId, userCrypto, objectSoftwareFmIds, groupId);
				for (String objectSoftwareFmId : objectSoftwareFmIds) {
					String objectCrypto = cryptoAccess.getCryptoForUser(objectSoftwareFmId);
					userMembership.remove(objectSoftwareFmId, objectCrypto, groupId, groupCrypto);
				}
				groups.removeUsers(groupId, groupCrypto, objectSoftwareFmIds);
				
			}});
		return IProcessResult.Utils.processString("");
	}

	private void checkMainUserIsAdminAndNoOthersAreAdmin(IUserMembership userMembership, String softwareFmId, String userCrypto, List<String> objectSoftwareFmIds, String groupId) {
		String mainStatus = userMembership.getMembershipProperty(softwareFmId, userCrypto, groupId, GroupConstants.membershipStatusKey);
		if (!GroupConstants.adminStatus.equals(mainStatus))
			throw new IllegalArgumentException(MessageFormat.format(GroupConstants.cannotKickUnlessAdmin, groupId, softwareFmId, mainStatus, objectSoftwareFmIds));
		for (String objectSoftwareFmId : objectSoftwareFmIds) {
			String objectCrypto = cryptoAccess.getCryptoForUser(objectSoftwareFmId);
			String objectsStatus = userMembership.getMembershipProperty(objectSoftwareFmId, objectCrypto, groupId, GroupConstants.membershipStatusKey);
			if (GroupConstants.adminStatus.equals(objectsStatus))
				throw new IllegalArgumentException(MessageFormat.format(GroupConstants.cannotKickAdmin, groupId, softwareFmId, mainStatus, objectSoftwareFmId));
		}
	}
}
