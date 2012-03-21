/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.softwareFm.crowdsource.api.server.AbstractProcessorDatabaseIntegrationTests;
import org.softwareFm.crowdsource.api.user.IGroups;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.api.user.IUserMembership;
import org.softwareFm.crowdsource.api.user.IUserMembershipReader;
import org.softwareFm.crowdsource.httpClient.IRequestBuilder;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.httpClient.internal.MemoryResponseCallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback2;
import org.softwareFm.crowdsource.utilities.collections.Iterables;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction2;
import org.softwareFm.crowdsource.utilities.maps.Maps;

public class InviteGroupProcessorTest extends AbstractProcessorDatabaseIntegrationTests {

	private final String fromEmail = "from@some.email";
	private String fromSoftwareFmId;

	@SuppressWarnings("unchecked")
	public void testTakeOnGroupWhenEverythingCorrect() throws Exception {
		final String groupId = getIdAndSaltGenerator().makeNewGroupId();
		api.makeReadWriter().modifyUserMembership(new ICallback2<IGroups, IUserMembership>() {
			@Override
			public void process(IGroups groups, IUserMembership membershipForServer) throws Exception {
				groups.setGroupProperty(groupId, groupCryptoKey0, GroupConstants.groupNameKey, "someGroupName");
				membershipForServer.addMembership(fromSoftwareFmId, userKey0, groupId, groupCryptoKey0, GroupConstants.adminStatus);
				groups.addUser(groupId, groupCryptoKey0, Maps.stringObjectMap(//
						LoginConstants.softwareFmIdKey, fromSoftwareFmId, //
						GroupConstants.membershipStatusKey, GroupConstants.adminStatus));
			}
		});
		getHttpClient().post(GroupConstants.inviteCommandPrefix).//
				addParam(GroupConstants.groupIdKey, groupId).//
				addParam(GroupConstants.takeOnSubjectKey, "someSubject").//
				addParam(LoginConstants.softwareFmIdKey, fromSoftwareFmId).//
				addParam(GroupConstants.takeOnFromKey, fromEmail).//
				addParam(GroupConstants.takeOnEmailPattern, "emailPattern: " + GroupConstants.emailMarker + "/" + GroupConstants.groupNameMarker).//
				addParam(GroupConstants.takeOnEmailListKey, "email1@a.b,email2@a.b").//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, "")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);

		api.makeReader().accessUserMembershipReader(new IFunction2<IGroupsReader, IUserMembershipReader, Void>() {
			@Override
			public Void apply(IGroupsReader groups, IUserMembershipReader from2) {
				List<Map<String, Object>> actual = Iterables.list(groups.users(groupId, groupCryptoKey0));
				List<Map<String, Object>> expected = Arrays.asList(//
						Maps.stringObjectMap(LoginConstants.softwareFmIdKey, fromSoftwareFmId, GroupConstants.membershipStatusKey, GroupConstants.adminStatus),//
						Maps.stringObjectMap(LoginConstants.softwareFmIdKey, "someNewSoftwareFmId1", "with", "enrich_0", LoginConstants.emailKey, "email1@a.b", GroupConstants.membershipStatusKey, GroupConstants.invitedStatus),//
						Maps.stringObjectMap(LoginConstants.softwareFmIdKey, "someNewSoftwareFmId2", "with", "enrich_1", LoginConstants.emailKey, "email2@a.b", GroupConstants.membershipStatusKey, GroupConstants.invitedStatus));
				assertEquals(expected, actual);
				return null;
			}

		});

		assertEquals(Lists.times(2, fromEmail), mailerMock.froms);
		assertEquals(Arrays.asList("email1@a.b", "email2@a.b"), mailerMock.tos);
		assertEquals(Lists.times(2, "someSubject"), mailerMock.subjects);
		assertEquals(Arrays.asList("emailPattern: email1@a.b/someGroupName", "emailPattern: email2@a.b/someGroupName"), mailerMock.messages);
	}

	public void testExceptionIfNotAdmin() throws Exception {
		final String groupId =  getIdAndSaltGenerator().makeNewGroupId();
		api.makeReadWriter().modifyUserMembership(new ICallback2<IGroups, IUserMembership>() {
			@Override
			public void process(IGroups groups, IUserMembership membershipForServer) throws Exception {
				groups.setGroupProperty(groupId, groupCryptoKey0, GroupConstants.groupNameKey, "someGroupName");
				membershipForServer.addMembership(fromSoftwareFmId, userKey0, groupId, groupCryptoKey0, "notAdmin");
				groups.addUser(groupId, groupCryptoKey0, Maps.stringObjectMap(//
						LoginConstants.softwareFmIdKey, fromSoftwareFmId, //
						GroupConstants.membershipStatusKey, "notAdmin"));
			}
		});
		getHttpClient().post(GroupConstants.inviteCommandPrefix).//
				addParam(GroupConstants.groupIdKey, groupId).//
				addParam(GroupConstants.takeOnSubjectKey, "someSubject").//
				addParam(LoginConstants.softwareFmIdKey, fromSoftwareFmId).//
				addParam(GroupConstants.takeOnFromKey, fromEmail).//
				addParam(GroupConstants.takeOnEmailPattern, "emailPattern: " + GroupConstants.emailMarker + "/" + GroupConstants.groupNameMarker).//
				addParam(GroupConstants.takeOnEmailListKey, "email1@a.b,email2@a.b").//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.serverErrorCode, "class java.lang.IllegalArgumentException/Cannot invite other people to group someGroupName as you are not admin. You are status notAdmin")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
		api.makeReader().accessUserMembershipReader(new IFunction2<IGroupsReader, IUserMembershipReader, Void>() {
			@Override
			public Void apply(IGroupsReader groups, IUserMembershipReader second) {
				List<Map<String, Object>> actual = Iterables.list(groups.users(groupId, groupCryptoKey0));
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> expected = Arrays.asList(//
						Maps.stringObjectMap(LoginConstants.softwareFmIdKey, fromSoftwareFmId, GroupConstants.membershipStatusKey, "notAdmin"));
				assertEquals(expected, actual);

				assertEquals(0, mailerMock.froms.size());
				return null;
			}
		});
	}

	public void testExceptionIfNotMemberAtAll() throws Exception {
		final String groupId =  getIdAndSaltGenerator().makeNewGroupId();
		api.makeReadWriter().modifyUserMembership(new ICallback2<IGroups, IUserMembership>() {
			@Override
			public void process(IGroups groups, IUserMembership second) throws Exception {
				groups.setGroupProperty(groupId, groupCryptoKey0, GroupConstants.groupNameKey, "someGroupName");
			}
		});
		getHttpClient().post(GroupConstants.inviteCommandPrefix).//
				addParam(GroupConstants.groupIdKey, groupId).//
				addParam(GroupConstants.takeOnSubjectKey, "someSubject").//
				addParam(LoginConstants.softwareFmIdKey, fromSoftwareFmId).//
				addParam(GroupConstants.takeOnFromKey, fromEmail).//
				addParam(GroupConstants.takeOnEmailPattern, "emailPattern: " + GroupConstants.emailMarker + "/" + GroupConstants.groupNameMarker).//
				addParam(GroupConstants.takeOnEmailListKey, "email1@a.b,email2@a.b").//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.serverErrorCode, "class java.lang.IllegalArgumentException/User someNewSoftwareFmId0 is not a member of group groupId0")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);

		api.makeReadWriter().modifyUserMembership(new ICallback2<IGroups, IUserMembership>() {
			@Override
			public void process(IGroups groups, IUserMembership second) throws Exception {
				List<Map<String, Object>> actual = Iterables.list(groups.users(groupId, groupCryptoKey0));
				List<Map<String, Object>> expected = Collections.emptyList();
				assertEquals(expected, actual);
			}
		});

		assertEquals(0, mailerMock.froms.size());
	}

	public void testThrowsExceptionsAndAddNoUsersIfPropertiesNotFullySet() throws Exception {
		checkCannotInviteOn(null, "sfmId", "someSubject", "someFrom", "emailPattern: $email$", "email1@a.b,email2@a.b,email3@a.b");
		checkCannotInviteOn("someNewGroupName", null, "someSubject", "someFrom", "emailPattern: $email$", "email1@a.b,email2@a.b,email3@a.b");
		checkCannotInviteOn("someNewGroupName", "sfmId", null, "someFrom", "emailPattern: $email$", "email1@a.b,email2@a.b,email3@a.b");
		checkCannotInviteOn("someNewGroupName", "sfmId", "someSubject", null, "emailPattern: $email$", "email1@a.b,email2@a.b,email3@a.b");
		checkCannotInviteOn("someNewGroupName", "sfmId", "someSubject", "someFrom", null, "email1@a.b,email2@a.b,email3@a.b");
		checkCannotInviteOn("someNewGroupName", "sfmId", "someSubject", "someFrom", "emailPattern: $email$", null);
		checkCannotInviteOn("someNewGroupName", "sfmId", "someSubject", "someFrom", "emailPattern: $email$", "email1");
		checkCannotInviteOn("someNewGroupName", "sfmId", "someSubject", "someFrom", "emailPattern: $email$", "email1,email2@a.b");

	}

	public void testThrowsExceptionIfFromEmailNotAnEmail() throws InterruptedException, ExecutionException, TimeoutException {
		getHttpClient().post(GroupConstants.takeOnCommandPrefix).//
				addParam(GroupConstants.groupNameKey, "someNewGroupName").//
				addParam(GroupConstants.takeOnSubjectKey, "someSubject").//
				addParam(LoginConstants.softwareFmIdKey, fromSoftwareFmId).//
				addParam(GroupConstants.takeOnFromKey, "wrongEmail").//
				addParam(GroupConstants.takeOnEmailPattern, "emailPattern: " + GroupConstants.emailMarker + "/" + GroupConstants.groupNameMarker).//
				addParam(GroupConstants.takeOnEmailListKey, "email1@a.b,email2@a.b").//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.serverErrorCode, "class java.lang.IllegalArgumentException/Invalid email wrongEmail")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
	}

	public void testThrowsExceptionIfOneOfTheEmailListIsNotAnEmail() throws Exception {
		getHttpClient().post(GroupConstants.takeOnCommandPrefix).//
				addParam(GroupConstants.groupNameKey, "someNewGroupName").//
				addParam(GroupConstants.takeOnSubjectKey, "someSubject").//
				addParam(LoginConstants.softwareFmIdKey, fromSoftwareFmId).//
				addParam(GroupConstants.takeOnFromKey, "wrongEmail").//
				addParam(GroupConstants.takeOnEmailPattern, "emailPattern: " + GroupConstants.emailMarker + "/" + GroupConstants.groupNameMarker).//
				addParam(GroupConstants.takeOnEmailListKey, "email1@a.b,email2@a.b,notAnEmail").//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.serverErrorCode, "class java.lang.IllegalArgumentException/Invalid email wrongEmail")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
	}

	public void testThrowsExceptionIfEmailAndSoftwareFmNotForSameUser() throws Exception {
		getHttpClient().post(GroupConstants.takeOnCommandPrefix).//
				addParam(GroupConstants.groupNameKey, "someNewGroupName").//
				addParam(GroupConstants.takeOnSubjectKey, "someSubject").//
				addParam(LoginConstants.softwareFmIdKey, fromSoftwareFmId).//
				addParam(GroupConstants.takeOnFromKey, "wrongEmail@a.b").//
				addParam(GroupConstants.takeOnEmailPattern, "emailPattern: " + GroupConstants.emailMarker + "/" + GroupConstants.groupNameMarker).//
				addParam(GroupConstants.takeOnEmailListKey, "email1@a.b,email2@a.b").//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.serverErrorCode, "class java.lang.IllegalArgumentException/Email / SoftwareFm mismatch. Email wrongEmail@a.b Expected null SoftwareFmId someNewSoftwareFmId0")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
	}

	protected void checkCannotInviteOn(String groupId, String softwareFmIdKey, String subject, String from, String emailPattern, String emailList) throws Exception {
		IRequestBuilder builder = getHttpClient().post(GroupConstants.takeOnCommandPrefix);
		add(builder, GroupConstants.groupIdKey, groupId);//
		add(builder, LoginConstants.softwareFmIdKey, softwareFmIdKey);//
		add(builder, GroupConstants.takeOnSubjectKey, subject);//
		add(builder, GroupConstants.takeOnFromKey, from);//
		add(builder, GroupConstants.takeOnEmailPattern, emailPattern);//
		add(builder, GroupConstants.takeOnEmailListKey, emailList);//
		MemoryResponseCallback memoryCallback = IResponseCallback.Utils.memoryCallback();
		builder.execute(memoryCallback).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
		memoryCallback.assertCalledOnce();
		assertEquals(500, memoryCallback.response.statusCode());
		assertTrue(memoryCallback.response.asString(), memoryCallback.response.asString().startsWith("class java.lang.IllegalAr"));
		if (groupId != null) {//just stops exception being thrown when no group id
			String url = GroupConstants.groupsGenerator(getUrlPrefix()).findUrlFor(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId));
			File groupDirectory = new File(remoteRoot, url);
			assertFalse(groupDirectory.exists());
			assertEquals(0, mailerMock.froms.size());
		}
	}

	private void add(IRequestBuilder builder, String key, String value) {
		if (value != null)
			builder.addParam(key, value);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		fromSoftwareFmId =  getIdAndSaltGenerator().makeNewUserId();
		api.getServerDoers().getSignUpChecker().signUp(fromEmail, "someMoniker", "someSalt", "irrelevant", fromSoftwareFmId);
	}

}