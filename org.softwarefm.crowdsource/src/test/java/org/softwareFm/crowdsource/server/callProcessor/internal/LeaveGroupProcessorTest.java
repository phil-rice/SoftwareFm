package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.util.Map;

import org.softwareFm.crowdsource.api.server.AbstractProcessCallTest;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.api.user.IGroups;
import org.softwareFm.crowdsource.api.user.IUserMembership;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback2;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.tests.Tests;

public class LeaveGroupProcessorTest extends AbstractProcessCallTest<LeaveGroupCommandProcessor> {

	public void testIgnoresNonePost() throws Exception {
		checkIgnoresNonePosts();
		checkIgnores(CommonConstants.POST); // wrong url
	}

	public void testLeavesGroup() {
		truncateUsersTable();
		assertEquals(softwareFmId0, createUser());
		assertEquals(groupId0, createGroup("name", groupCryptoKey0));
		getServerUserAndGroupsContainer().accessUserMembership(new ICallback2<IGroups, IUserMembership>() {
			@Override
			public void process(IGroups groups, IUserMembership userMembership) throws Exception {
				groups.addUser(groupId0, groupCryptoKey0, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId0, GroupConstants.membershipStatusKey, GroupConstants.adminStatus));
				userMembership.addMembership(softwareFmId0, userKey0, groupId0, groupCryptoKey0, GroupConstants.invitedStatus);
			}
		});

		IProcessResult result = processor.execute(GroupConstants.leaveGroupPrefix, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId0, GroupConstants.groupIdKey, groupId0));
		checkStringResult(result, "");
	}

	public void testThrowsExceptionifNeededParametersAreNotPresent() {
		checkException(softwareFmId0, null, "groupId, {softwareFmId=someNewSoftwareFmId0}");
		checkException(null, groupId0, "softwareFmId, {groupCrypto=groupId0}");
	}

	private void checkException(String softwareFmId, String groupId, String expected) {
		final Map<String, Object> parameters = Maps.newMap();
		if (softwareFmId != null)
			parameters.put(LoginConstants.softwareFmIdKey, softwareFmId);
		if (groupId != null)
			parameters.put(GroupConstants.groupCryptoKey, groupId);
		Tests.assertThrowsWithMessage(expected, IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				processor.execute(GroupConstants.acceptInvitePrefix, parameters);
			}
		});
	}

	@Override
	protected LeaveGroupCommandProcessor makeProcessor() {
		return new LeaveGroupCommandProcessor(getServerUserAndGroupsContainer(), getUserCryptoAccess());
	}

}
