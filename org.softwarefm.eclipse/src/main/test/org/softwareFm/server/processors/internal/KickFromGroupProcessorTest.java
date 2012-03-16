package org.softwareFm.server.processors.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.common.arrays.ArrayHelper;
import org.softwareFm.common.collections.Iterables;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.server.processors.AbstractProcessorDatabaseIntegrationTests;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.ProcessCallParameters;
import org.softwareFm.softwareFmServer.KickFromGroupCommandProcessor;

public class KickFromGroupProcessorTest extends AbstractProcessorDatabaseIntegrationTests {
	private final String adminId = "someNewSoftwareFmId0";
	private final String objectId = "someNewSoftwareFmId1";

	@SuppressWarnings("unchecked")
	public void testWhenUserIsAdminCanKickAnotherPerson() throws Exception {
		createGroup(GroupConstants.adminStatus, "initialStatus");

		getHttpClient().post(GroupConstants.kickFromGroupPrefix).//
				addParam(GroupConstants.groupIdKey, groupId).//
				addParam(LoginConstants.softwareFmIdKey, adminId).//
				addParam(GroupConstants.objectSoftwareFmId, objectId).//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, "")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);

		assertEquals(Arrays.asList(Maps.stringObjectMap(GroupConstants.membershipStatusKey, GroupConstants.adminStatus, LoginConstants.softwareFmIdKey, adminId, "a", "b")), Iterables.list(groups.users(groupId, groupCryptoKey)));
		assertEquals(Collections.emptyList(), membershipForServer.walkGroupsFor(objectId, userKey2));

		assertEquals(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.groupCryptoKey, groupCryptoKey, GroupConstants.membershipStatusKey, GroupConstants.adminStatus), Iterables.getOnly(membershipForServer.walkGroupsFor(adminId, userCryptoKey)));
	}

	@SuppressWarnings("unchecked")
	public void testEvenWhenAdminCannotKickAnotherAdmin() throws Exception {
		createGroup(GroupConstants.adminStatus, GroupConstants.adminStatus);

		getHttpClient().post(GroupConstants.kickFromGroupPrefix).//
				addParam(GroupConstants.groupIdKey, groupId).//
				addParam(LoginConstants.softwareFmIdKey, adminId).//
				addParam(GroupConstants.objectSoftwareFmId, objectId).//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.serverErrorCode, "class java.lang.IllegalArgumentException/Cannot kick admin.\n" + //
						"Group someGroupId\n" + //
						"User someNewSoftwareFmId0\n" + //
						"Users status admin\n" + //
						"Object SoftwareFmId someNewSoftwareFmId1")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);

		assertEquals(Arrays.asList(//
				Maps.stringObjectMap(GroupConstants.membershipStatusKey, GroupConstants.adminStatus, LoginConstants.softwareFmIdKey, adminId, "a", "b"),//
				Maps.stringObjectMap(GroupConstants.membershipStatusKey, GroupConstants.adminStatus, LoginConstants.softwareFmIdKey, objectId, "a", "b")), Iterables.list(groups.users(groupId, groupCryptoKey)));

		assertEquals(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.groupCryptoKey, groupCryptoKey, GroupConstants.membershipStatusKey, GroupConstants.adminStatus), Iterables.getOnly(membershipForServer.walkGroupsFor(adminId, userCryptoKey)));
		assertEquals(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.groupCryptoKey, groupCryptoKey, GroupConstants.membershipStatusKey, GroupConstants.adminStatus), Iterables.getOnly(membershipForServer.walkGroupsFor(objectId, userKey2)));
	}

	@SuppressWarnings("unchecked")
	public void testWhenUserIsNotAdminCannotKickAnotherPerson() throws Exception {
		createGroup("nonAdminStatus", "initialStatus");

		getHttpClient().post(GroupConstants.kickFromGroupPrefix).//
				addParam(GroupConstants.groupIdKey, groupId).//
				addParam(LoginConstants.softwareFmIdKey, adminId).//
				addParam(GroupConstants.objectSoftwareFmId, objectId).//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.serverErrorCode, "class java.lang.IllegalArgumentException/Cannot kick unless admin.\n Group someGroupId\nUser someNewSoftwareFmId0\nUsers status nonAdminStatus\nObject SoftwareFmIds [someNewSoftwareFmId1]")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);

		assertEquals(Arrays.asList(//
				Maps.stringObjectMap(GroupConstants.membershipStatusKey, "nonAdminStatus", LoginConstants.softwareFmIdKey, adminId, "a", "b"),//
				Maps.stringObjectMap(GroupConstants.membershipStatusKey, "initialStatus", LoginConstants.softwareFmIdKey, objectId, "a", "b")), Iterables.list(groups.users(groupId, groupCryptoKey)));

		assertEquals(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.groupCryptoKey, groupCryptoKey, GroupConstants.membershipStatusKey, "nonAdminStatus"), Iterables.getOnly(membershipForServer.walkGroupsFor(adminId, userCryptoKey)));
		assertEquals(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.groupCryptoKey, groupCryptoKey, GroupConstants.membershipStatusKey, "initialStatus"), Iterables.getOnly(membershipForServer.walkGroupsFor(objectId, userKey2)));
	}

	private void createGroup(String subjectStatus, String otherStatus) {
		groups.setGroupProperty(groupId, groupCryptoKey, GroupConstants.groupNameKey, "someName");
		user.setUserProperty(adminId, userCryptoKey, LoginConstants.emailKey, "someEmail");
		user.setUserProperty(objectId, userKey2, LoginConstants.emailKey, "someEmail1");
		groups.addUser(groupId, groupCryptoKey, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, adminId, GroupConstants.membershipStatusKey, subjectStatus, "a", "b"));
		membershipForServer.addMembership(adminId, userCryptoKey, groupId, groupCryptoKey, subjectStatus);

		groups.addUser(groupId, groupCryptoKey, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, objectId, GroupConstants.membershipStatusKey, otherStatus, "a", "b"));
		membershipForServer.addMembership(objectId, userKey2, groupId, groupCryptoKey, otherStatus);
	}

	@Override
	protected IFunction1<ProcessCallParameters, IProcessCall[]> getExtraProcessCalls() {
		return new IFunction1<ProcessCallParameters, IProcessCall[]>() {
			@Override
			public IProcessCall[] apply(ProcessCallParameters from) throws Exception {
				IProcessCall[] parents = KickFromGroupProcessorTest.super.getExtraProcessCalls().apply(from);
				return ArrayHelper.insert(parents, new KickFromGroupCommandProcessor(groups, membershipForServer, userCryptoFn));
			}
		};
	}

}
