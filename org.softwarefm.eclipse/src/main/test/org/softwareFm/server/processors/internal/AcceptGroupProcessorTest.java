package org.softwareFm.server.processors.internal;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.softwareFm.client.http.requests.IRequestBuilder;
import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.common.collections.Iterables;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.server.processors.AbstractProcessorDatabaseIntegrationTests;

public class AcceptGroupProcessorTest extends AbstractProcessorDatabaseIntegrationTests {

	private final String softwareFmId = "someNewSoftwareFmId0";

	@SuppressWarnings("unchecked")
	public void testAcceptChangesStatusToMember() throws Exception {
		user.setUserProperty(softwareFmId, userCryptoKey, LoginConstants.emailKey, "someEmail");
		groups.setGroupProperty(groupId, groupCryptoKey, GroupConstants.groupNameKey, "someName");

		groups.addUser(groupId, groupCryptoKey, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId, GroupConstants.membershipStatusKey, "initialStatus", "a", "b"));
		membershipForServer.addMembership(softwareFmId, userCryptoKey, groupId, groupCryptoKey, "initialStatus");

		getHttpClient().post(GroupConstants.acceptInvitePrefix).//
				addParam(GroupConstants.groupIdKey, groupId).//
				addParam(LoginConstants.softwareFmIdKey, softwareFmId).//
				addParam(GroupConstants.membershipStatusKey, "newStatus").//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, "")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
		assertEquals(Arrays.asList(Maps.stringObjectMap(GroupConstants.membershipStatusKey, "newStatus", LoginConstants.softwareFmIdKey, softwareFmId, "a", "b")), Iterables.list(groups.users(groupId, groupCryptoKey)));
		assertEquals(Arrays.asList(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.groupCryptoKey, groupCryptoKey, GroupConstants.membershipStatusKey, "newStatus")), membershipForServer.walkGroupsFor(softwareFmId, userCryptoKey));
	}

	public void testThrowsExceptionIfNotEnoughParametersAreSent() throws Exception {
		checkThrowsException(null, softwareFmId, "newStatus", "class java.lang.IllegalArgumentException/groupId, {membershipStatus=newStatus, softwareFmId=someNewSoftwareFmId0}");
		checkThrowsException(groupId, null, "newStatus", "class java.lang.IllegalArgumentException/softwareFmId, {groupId=someGroupId, membershipStatus=newStatus}");
		checkThrowsException(groupId, softwareFmId, null, "class java.lang.IllegalArgumentException/membershipStatus, {groupId=someGroupId, softwareFmId=someNewSoftwareFmId0}");
	}

	private void checkThrowsException(String groupId, String softwareFmId, String status, String error) throws Exception {
		IRequestBuilder requestBuilder = getHttpClient().post(GroupConstants.acceptInvitePrefix);
		add(requestBuilder, GroupConstants.groupIdKey, groupId);//
		add(requestBuilder, LoginConstants.softwareFmIdKey, softwareFmId);//
		add(requestBuilder, GroupConstants.membershipStatusKey, status);//
		requestBuilder.execute(IResponseCallback.Utils.checkCallback(CommonConstants.serverErrorCode, error)).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);

	}

	private void add(IRequestBuilder requestBuilder, String key, String value) {
		if (value != null)
			requestBuilder.addParam(key, value);
	}

	public void testThrowsExceptionIfNotAMemberOfGroup() throws Exception {
		getHttpClient().post(GroupConstants.acceptInvitePrefix).//
				addParam(GroupConstants.groupIdKey, groupId).//
				addParam(LoginConstants.softwareFmIdKey, softwareFmId).//
				addParam(GroupConstants.membershipStatusKey, "newStatus").//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.serverErrorCode, "class java.lang.IllegalArgumentException/User someNewSoftwareFmId0 is not a member of group someGroupId")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
	}
}
