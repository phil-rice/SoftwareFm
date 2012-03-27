package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.softwareFm.crowdsource.api.server.AbstractProcessorDatabaseIntegrationTests;
import org.softwareFm.crowdsource.api.user.IGroups;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.api.user.IUserMembership;
import org.softwareFm.crowdsource.api.user.IUserMembershipReader;
import org.softwareFm.crowdsource.httpClient.IRequestBuilder;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback2;
import org.softwareFm.crowdsource.utilities.collections.Iterables;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction2;
import org.softwareFm.crowdsource.utilities.maps.Maps;

public class AcceptGroupProcessorTest extends AbstractProcessorDatabaseIntegrationTests {

	public String softwareFmId;
	private String groupId;

	@SuppressWarnings("unchecked")
	public void testAcceptChangesStatusToMember() throws Exception {
		createSoftwareFmUserAndGroupId0();
		addUserToGroup();

		getHttpClient().post(GroupConstants.acceptInvitePrefix).//
				addParam(GroupConstants.groupIdKey, groupId).//
				addParam(LoginConstants.softwareFmIdKey, softwareFmId).//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, "")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
		userAndGroupsContainer.accessUserMembershipReader(new IFunction2<IGroupsReader, IUserMembershipReader, Void>() {
			@Override
			public Void apply(IGroupsReader groups, IUserMembershipReader membership) throws Exception {
				assertEquals(Arrays.asList(Maps.stringObjectMap(GroupConstants.membershipStatusKey, GroupConstants.memberStatus, LoginConstants.softwareFmIdKey, softwareFmId, "a", "b")), Iterables.list(groups.users(groupId, groupCryptoKey0)));
				assertEquals(Arrays.asList(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.groupCryptoKey, groupCryptoKey0, GroupConstants.membershipStatusKey, GroupConstants.memberStatus)), membership.walkGroupsFor(softwareFmId, userKey0));
				return null;
			}
		}, ICallback.Utils.<Void>noCallback()).get();
	}

	private void createSoftwareFmUserAndGroupId0() {
		softwareFmId= createUser();
		userAndGroupsContainer.accessGroups(new ICallback<IGroups>() {
			@Override
			public void process(IGroups groups) throws Exception {
				groups.setGroupProperty(groupId = getIdAndSaltGenerator().makeNewGroupId(), groupCryptoKey0, GroupConstants.groupNameKey, "someName");
			}
		}).get();
	}

	private void addUserToGroup() {
		userAndGroupsContainer.accessUserMembership(new ICallback2<IGroups, IUserMembership>() {
			@Override
			public void process(IGroups groups, IUserMembership membership) throws Exception {
				groups.addUser(groupId, groupCryptoKey0, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId, GroupConstants.membershipStatusKey, "initialStatus", "a", "b"));
				membership.addMembership(softwareFmId, userKey0, groupId, groupCryptoKey0, "initialStatus");
			}
		}).get();
	}

	public void testThrowsExceptionIfNotEnoughParametersAreSent() throws Exception {
		createSoftwareFmUserAndGroupId0();

		checkThrowsException(null, softwareFmId, "class java.lang.IllegalArgumentException/groupId, {softwareFmId=someNewSoftwareFmId0}");
		checkThrowsException(groupId, null, "class java.lang.IllegalArgumentException/softwareFmId, {groupId=groupId0}");
	}

	private void checkThrowsException(String groupId, String softwareFmId, String error) throws Exception {
		IRequestBuilder requestBuilder = getHttpClient().post(GroupConstants.acceptInvitePrefix);
		add(requestBuilder, GroupConstants.groupIdKey, groupId);//
		add(requestBuilder, LoginConstants.softwareFmIdKey, softwareFmId);//
		requestBuilder.execute(IResponseCallback.Utils.checkCallback(CommonConstants.serverErrorCode, error)).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);

	}

	private void add(IRequestBuilder requestBuilder, String key, String value) {
		if (value != null)
			requestBuilder.addParam(key, value);
	}

	public void testThrowsExceptionIfNotAMemberOfGroup() throws Exception {createSoftwareFmUserAndGroupId0();
		getHttpClient().post(GroupConstants.acceptInvitePrefix).//
				addParam(GroupConstants.groupIdKey, groupId).//
				addParam(LoginConstants.softwareFmIdKey, softwareFmId).//
				addParam(GroupConstants.membershipStatusKey, "newStatus").//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.serverErrorCode, "class java.lang.IllegalArgumentException/User someNewSoftwareFmId0 is not a member of group groupId0")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
	}
}
