package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.util.Arrays;
import java.util.Map;

import org.softwareFm.crowdsource.api.server.AbstractProcessCallTest;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.api.user.IGroups;
import org.softwareFm.crowdsource.api.user.IUserMembership;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback2;
import org.softwareFm.crowdsource.utilities.collections.Iterables;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.tests.Tests;

public class AcceptInviteProcessorTest extends AbstractProcessCallTest<AcceptInviteGroupProcessor> {

	public void testIgnoresNonePost() throws Exception {
		checkIgnoresNonePosts();
		checkIgnores(CommonConstants.POST); // wrong url
	}

	public void testAcceptsInvite() {
		truncateUsersTable();
		assertEquals(softwareFmId0, createUser());
		assertEquals(softwareFmId1, createUser());
		assertEquals(groupId0, createGroup("name1", groupCryptoKey0));
		assertEquals(groupId1, createGroup("name2", groupCryptoKey2));
		getServerUserAndGroupsContainer().accessUserMembership(new ICallback2<IGroups, IUserMembership>() {
			@Override
			public void process(IGroups groups, IUserMembership userMembership) throws Exception {
				groups.addUser(groupId0, groupCryptoKey0, sfm0Invited);
				groups.addUser(groupId0, groupCryptoKey0, sfm1Admin);
				userMembership.addMembership(softwareFmId0, userKey0, groupId0, groupCryptoKey0, GroupConstants.invitedStatus);
				userMembership.addMembership(softwareFmId1, userKey1, groupId0, groupCryptoKey0, GroupConstants.adminStatus);

				groups.addUser(groupId1, groupCryptoKey1, sfm0Member);
				userMembership.addMembership(softwareFmId0, userKey0, groupId1, groupCryptoKey1, GroupConstants.invitedStatus);
			}
		}).get();

		IProcessResult result = processor.execute(GroupConstants.acceptInvitePrefix, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId0, GroupConstants.groupIdKey, groupId0));
		checkStringResult(result, "");
		getServerUserAndGroupsContainer().accessUserMembership(new ICallback2<IGroups, IUserMembership>() {
			@SuppressWarnings("unchecked")
			@Override
			public void process(IGroups groups, IUserMembership userMembership) throws Exception {
				assertEquals(Arrays.asList(sfm0Member, sfm1Admin), Iterables.list(groups.users(groupId0, groupCryptoKey0)));
				assertEquals(Arrays.asList(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId0, GroupConstants.groupCryptoKey, groupCryptoKey0, GroupConstants.membershipStatusKey, GroupConstants.memberStatus),//
						Maps.stringObjectMap(GroupConstants.groupIdKey, groupId1, GroupConstants.groupCryptoKey, groupCryptoKey1, GroupConstants.membershipStatusKey, GroupConstants.invitedStatus)), Iterables.list(userMembership.walkGroupsFor(softwareFmId0, userKey0)));
			}
		});
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
	protected AcceptInviteGroupProcessor makeProcessor() {
		return new AcceptInviteGroupProcessor(getUserCryptoAccess(), getServerUserAndGroupsContainer());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
}
