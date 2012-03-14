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

	public void testWhenUserIsAdminCanKickAnotherPerson() throws Exception {
		createGroupWithAdminHavingStatus(GroupConstants.adminStatus);

		getHttpClient().post(GroupConstants.kickFromGroupPrefix).//
				addParam(GroupConstants.groupIdKey, groupId).//
				addParam(LoginConstants.softwareFmIdKey, adminId).//
				addParam(GroupConstants.objectSoftwareFmId, objectId).//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, "")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);

		assertEquals(Arrays.asList(Maps.stringObjectMap(GroupConstants.membershipStatusKey, GroupConstants.adminStatus, LoginConstants.softwareFmIdKey, adminId, "a", "b")), Iterables.list(groups.users(groupId, groupCryptoKey)));
		assertEquals(Collections.emptyList(), membershipForServer.walkGroupsFor(objectId, userKey2));

		assertEquals(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.groupCryptoKey, groupCryptoKey, GroupConstants.membershipStatusKey, GroupConstants.adminStatus), Iterables.getOnly(membershipForServer.walkGroupsFor(adminId, userCryptoKey)));
	}

	public void testWhenUserIsNotAdminCannotKickAnotherPerson() throws Exception {
		createGroupWithAdminHavingStatus("nonAdminStatus");

		getHttpClient().post(GroupConstants.kickFromGroupPrefix).//
				addParam(GroupConstants.groupIdKey, groupId).//
				addParam(LoginConstants.softwareFmIdKey, adminId).//
				addParam(GroupConstants.objectSoftwareFmId, objectId).//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.serverErrorCode, "class java.lang.IllegalArgumentException/Cannot kick unless admin.\n Group someGroupId\nUser someNewSoftwareFmId0\nUsers status nonAdminStatus\nObject SoftwareFmId someNewSoftwareFmId1")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);

		assertEquals(Arrays.asList(//
				Maps.stringObjectMap(GroupConstants.membershipStatusKey, "nonAdminStatus", LoginConstants.softwareFmIdKey, adminId, "a", "b"),//
				Maps.stringObjectMap(GroupConstants.membershipStatusKey, "initialStatus", LoginConstants.softwareFmIdKey, objectId, "a", "b")
				), Iterables.list(groups.users(groupId, groupCryptoKey)));

		assertEquals(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.groupCryptoKey, groupCryptoKey, GroupConstants.membershipStatusKey, "nonAdminStatus"), Iterables.getOnly(membershipForServer.walkGroupsFor(adminId, userCryptoKey)));
		assertEquals(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.groupCryptoKey, groupCryptoKey, GroupConstants.membershipStatusKey, "initialStatus"), Iterables.getOnly(membershipForServer.walkGroupsFor(objectId, userKey2)));
	}

	private void createGroupWithAdminHavingStatus(String adminStatus) {
		groups.setGroupProperty(groupId, groupCryptoKey, GroupConstants.groupNameKey, "someName");
		user.setUserProperty(adminId, userCryptoKey, LoginConstants.emailKey, "someEmail");
		user.setUserProperty(objectId, userKey2, LoginConstants.emailKey, "someEmail1");
		groups.addUser(groupId, groupCryptoKey, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, adminId, GroupConstants.membershipStatusKey, adminStatus, "a", "b"));
		membershipForServer.addMembership(adminId, userCryptoKey, groupId, groupCryptoKey, adminStatus);

		groups.addUser(groupId, groupCryptoKey, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, objectId, GroupConstants.membershipStatusKey, "initialStatus", "a", "b"));
		membershipForServer.addMembership(objectId, userKey2, groupId, groupCryptoKey, "initialStatus");
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
