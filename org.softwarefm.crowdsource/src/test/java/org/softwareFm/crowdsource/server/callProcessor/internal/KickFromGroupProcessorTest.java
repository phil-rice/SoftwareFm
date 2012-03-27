package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
import org.softwareFm.crowdsource.api.server.AbstractProcessorDatabaseIntegrationTests;
import org.softwareFm.crowdsource.api.server.ISignUpChecker;
import org.softwareFm.crowdsource.api.user.IGroups;
import org.softwareFm.crowdsource.api.user.IUser;
import org.softwareFm.crowdsource.api.user.IUserMembership;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback2;
import org.softwareFm.crowdsource.utilities.collections.Iterables;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.maps.Maps;

public class KickFromGroupProcessorTest extends AbstractProcessorDatabaseIntegrationTests {
	private String adminId;
	private String objectId;
	private String groupId;

	@SuppressWarnings("unchecked")
	public void testWhenUserIsAdminCanKickAnotherPerson() throws Exception {
		createUsersAndGroup(GroupConstants.adminStatus, "initialStatus");

		getHttpClient().post(GroupConstants.kickFromGroupPrefix).//
				addParam(GroupConstants.groupIdKey, groupId).//
				addParam(LoginConstants.softwareFmIdKey, adminId).//
				addParam(GroupConstants.objectSoftwareFmId, objectId).//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, "")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);

		api.makeUserAndGroupsContainer().accessUserMembership(new ICallback2<IGroups, IUserMembership>() {
			@Override
			public void process(IGroups groups, IUserMembership membershipForServer) throws Exception {
				assertEquals(Arrays.asList(Maps.stringObjectMap(GroupConstants.membershipStatusKey, GroupConstants.adminStatus, LoginConstants.softwareFmIdKey, adminId, "a", "b")), Iterables.list(groups.users(groupId, groupCryptoKey0)));
				assertEquals(Collections.emptyList(), membershipForServer.walkGroupsFor(objectId, userKey1));
				assertEquals(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.groupCryptoKey, groupCryptoKey0, GroupConstants.membershipStatusKey, GroupConstants.adminStatus), Iterables.getOnly(membershipForServer.walkGroupsFor(adminId, userKey0)));
			}
		}).get();
	}

	@SuppressWarnings("unchecked")
	public void testEvenWhenAdminCannotKickAnotherAdmin() throws Exception {
		createUsersAndGroup(GroupConstants.adminStatus, GroupConstants.adminStatus);

		getHttpClient().post(GroupConstants.kickFromGroupPrefix).//
				addParam(GroupConstants.groupIdKey, groupId).//
				addParam(LoginConstants.softwareFmIdKey, adminId).//
				addParam(GroupConstants.objectSoftwareFmId, objectId).//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.serverErrorCode, "class java.lang.IllegalArgumentException/Cannot kick admin.\n" + //
						"Group groupId0\n" + //
						"User someNewSoftwareFmId0\n" + //
						"Users status admin\n" + //
						"Object SoftwareFmId someNewSoftwareFmId1")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
		api.makeUserAndGroupsContainer().accessUserMembership(new ICallback2<IGroups, IUserMembership>() {
			@Override
			public void process(IGroups groups, IUserMembership membershipForServer) throws Exception {
				assertEquals(Arrays.asList(//
						Maps.stringObjectMap(GroupConstants.membershipStatusKey, GroupConstants.adminStatus, LoginConstants.softwareFmIdKey, adminId, "a", "b"),//
						Maps.stringObjectMap(GroupConstants.membershipStatusKey, GroupConstants.adminStatus, LoginConstants.softwareFmIdKey, objectId, "a", "b")), Iterables.list(groups.users(groupId, groupCryptoKey0)));

				assertEquals(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.groupCryptoKey, groupCryptoKey0, GroupConstants.membershipStatusKey, GroupConstants.adminStatus), Iterables.getOnly(membershipForServer.walkGroupsFor(adminId, userKey0)));
				assertEquals(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.groupCryptoKey, groupCryptoKey0, GroupConstants.membershipStatusKey, GroupConstants.adminStatus), Iterables.getOnly(membershipForServer.walkGroupsFor(objectId, userKey1)));
			}
		}).get();
	}

	@SuppressWarnings("unchecked")
	public void testWhenUserIsNotAdminCannotKickAnotherPerson() throws Exception {
		createUsersAndGroup("nonAdminStatus", "initialStatus");

		getHttpClient().post(GroupConstants.kickFromGroupPrefix).//
				addParam(GroupConstants.groupIdKey, groupId).//
				addParam(LoginConstants.softwareFmIdKey, adminId).//
				addParam(GroupConstants.objectSoftwareFmId, objectId).//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.serverErrorCode, "class java.lang.IllegalArgumentException/Cannot kick unless admin.\n Group groupId0\nUser someNewSoftwareFmId0\nUsers status nonAdminStatus\nObject SoftwareFmIds [someNewSoftwareFmId1]")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
		api.makeUserAndGroupsContainer().accessUserMembership(new ICallback2<IGroups, IUserMembership>() {
			@Override
			public void process(IGroups groups, IUserMembership membershipForServer) throws Exception {
				assertEquals(Arrays.asList(//
						Maps.stringObjectMap(GroupConstants.membershipStatusKey, "nonAdminStatus", LoginConstants.softwareFmIdKey, adminId, "a", "b"),//
						Maps.stringObjectMap(GroupConstants.membershipStatusKey, "initialStatus", LoginConstants.softwareFmIdKey, objectId, "a", "b")), Iterables.list(groups.users(groupId, groupCryptoKey0)));

				assertEquals(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.groupCryptoKey, groupCryptoKey0, GroupConstants.membershipStatusKey, "nonAdminStatus"), Iterables.getOnly(membershipForServer.walkGroupsFor(adminId, userKey0)));
				assertEquals(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.groupCryptoKey, groupCryptoKey0, GroupConstants.membershipStatusKey, "initialStatus"), Iterables.getOnly(membershipForServer.walkGroupsFor(objectId, userKey1)));
			}
		}).get();
	}

	private void createUsersAndGroup(final String subjectStatus, final String otherStatus) {
		ISignUpChecker signUpChecker = api.getServerDoers().getSignUpChecker();
		signUpChecker.signUp("someEmail", "monikor", "salt", "passwordHash", adminId = getIdAndSaltGenerator().makeNewUserId());
		signUpChecker.signUp("someEmail1", "monikor", "salt", "passwordHash", objectId = getIdAndSaltGenerator().makeNewUserId());
		groupId = getIdAndSaltGenerator().makeNewGroupId();
		IUserAndGroupsContainer container = api.makeUserAndGroupsContainer();
		container.accessUser(new ICallback<IUser>() {
			@Override
			public void process(IUser user) throws Exception {
				user.setUserProperty(adminId, userKey0, LoginConstants.emailKey, "someEmail");
				user.setUserProperty(objectId, userKey1, LoginConstants.emailKey, "someEmail1");
			}
		}).get();

		container.accessUserMembership(new ICallback2<IGroups, IUserMembership>() {
			@Override
			public void process(IGroups groups, IUserMembership membershipForServer) throws Exception {
				groups.setGroupProperty(groupId, groupCryptoKey0, GroupConstants.groupNameKey, "someName");
				groups.addUser(groupId, groupCryptoKey0, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, adminId, GroupConstants.membershipStatusKey, subjectStatus, "a", "b"));
				membershipForServer.addMembership(adminId, userKey0, groupId, groupCryptoKey0, subjectStatus);

				groups.addUser(groupId, groupCryptoKey0, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, objectId, GroupConstants.membershipStatusKey, otherStatus, "a", "b"));
				membershipForServer.addMembership(objectId, userKey1, groupId, groupCryptoKey0, otherStatus);
			}
		}).get();
	}

}
