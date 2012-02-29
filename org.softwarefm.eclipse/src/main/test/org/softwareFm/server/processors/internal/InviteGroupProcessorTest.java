/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.processors.internal;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.softwareFm.client.http.requests.IRequestBuilder;
import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.client.http.requests.MemoryResponseCallback;
import org.softwareFm.common.collections.Iterables;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.server.processors.AbstractProcessorDatabaseIntegrationTests;

public class InviteGroupProcessorTest extends AbstractProcessorDatabaseIntegrationTests {

	private final String fromEmail = "from@some.email";
	private String fromSoftwareFmId;

	@SuppressWarnings("unchecked")
	public void testTakeOnGroupWhenEverythingCorrect() throws Exception {
		groups.setGroupProperty(groupId, groupCryptoKey, GroupConstants.groupNameKey, "someGroupName");
		membershipForServer.addMembership(fromSoftwareFmId, userCryptoKey, groupId, groupCryptoKey, GroupConstants.adminStatus);
		groups.addUser(groupId, groupCryptoKey, Maps.stringObjectMap(//
				LoginConstants.softwareFmIdKey, fromSoftwareFmId, //
				GroupConstants.membershipStatusKey, GroupConstants.adminStatus,//
				SoftwareFmConstants.projectCryptoKey, processCallParameters.user.getUserProperty(fromSoftwareFmId, userCryptoKey, SoftwareFmConstants.projectCryptoKey)));
		getHttpClient().post(GroupConstants.inviteCommandPrefix).//
				addParam(GroupConstants.groupIdKey, groupId).//
				addParam(GroupConstants.takeOnSubjectKey, "someSubject").//
				addParam(LoginConstants.softwareFmIdKey, fromSoftwareFmId).//
				addParam(GroupConstants.takeOnFromKey, fromEmail).//
				addParam(GroupConstants.takeOnEmailPattern, "emailPattern: " + GroupConstants.emailMarker + "/" + GroupConstants.groupNameMarker).//
				addParam(GroupConstants.takeOnEmailListKey, "email1@a.b,email2@a.b").//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, "")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);

		List<Map<String, Object>> actual = Iterables.list(groups.users(groupId, groupCryptoKey));
		List<Map<String, Object>> expected = Arrays.asList(//
				Maps.stringObjectMap(LoginConstants.softwareFmIdKey, fromSoftwareFmId, SoftwareFmConstants.projectCryptoKey, projectCryptoKey1, GroupConstants.membershipStatusKey, GroupConstants.adminStatus),//
				Maps.stringObjectMap(LoginConstants.softwareFmIdKey, "someNewSoftwareFmId1", SoftwareFmConstants.projectCryptoKey, projectCryptoKey2, LoginConstants.emailKey, "email1@a.b", GroupConstants.membershipStatusKey, GroupConstants.invitedStatus),//
				Maps.stringObjectMap(LoginConstants.softwareFmIdKey, "someNewSoftwareFmId2", SoftwareFmConstants.projectCryptoKey, projectCryptoKey3, LoginConstants.emailKey, "email2@a.b", GroupConstants.membershipStatusKey, GroupConstants.invitedStatus));
		assertEquals(expected, actual);

		assertEquals(Lists.times(2, fromEmail), mailerMock.froms);
		assertEquals(Arrays.asList("email1@a.b", "email2@a.b"), mailerMock.tos);
		assertEquals(Lists.times(2, "someSubject"), mailerMock.subjects);
		assertEquals(Arrays.asList("emailPattern: email1@a.b/someGroupName", "emailPattern: email2@a.b/someGroupName"), mailerMock.messages);
	}

	public void testExceptionIfNotAdmin() throws Exception {
		groups.setGroupProperty(groupId, groupCryptoKey, GroupConstants.groupNameKey, "someGroupName");
		membershipForServer.addMembership(fromSoftwareFmId, userCryptoKey, groupId, groupCryptoKey, "notAdmin");
		groups.addUser(groupId, groupCryptoKey, Maps.stringObjectMap(//
				LoginConstants.softwareFmIdKey, fromSoftwareFmId, //
				GroupConstants.membershipStatusKey, "notAdmin",//
				SoftwareFmConstants.projectCryptoKey, processCallParameters.user.getUserProperty(fromSoftwareFmId, userCryptoKey, SoftwareFmConstants.projectCryptoKey)));
		getHttpClient().post(GroupConstants.inviteCommandPrefix).//
				addParam(GroupConstants.groupIdKey, groupId).//
				addParam(GroupConstants.takeOnSubjectKey, "someSubject").//
				addParam(LoginConstants.softwareFmIdKey, fromSoftwareFmId).//
				addParam(GroupConstants.takeOnFromKey, fromEmail).//
				addParam(GroupConstants.takeOnEmailPattern, "emailPattern: " + GroupConstants.emailMarker + "/" + GroupConstants.groupNameMarker).//
				addParam(GroupConstants.takeOnEmailListKey, "email1@a.b,email2@a.b").//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.serverErrorCode, "class java.lang.IllegalArgumentException/Cannot invite other people to group someGroupName as you are not admin. You are status notAdmin")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);

		List<Map<String, Object>> actual = Iterables.list(groups.users(groupId, groupCryptoKey));
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> expected = Arrays.asList(//
				Maps.stringObjectMap(LoginConstants.softwareFmIdKey, fromSoftwareFmId, SoftwareFmConstants.projectCryptoKey, projectCryptoKey1, GroupConstants.membershipStatusKey, "notAdmin"));
		assertEquals(expected, actual);

		assertEquals(0, mailerMock.froms.size());
	}

	public void testExceptionIfNotMemberAtAll() throws Exception {
		groups.setGroupProperty(groupId, groupCryptoKey, GroupConstants.groupNameKey, "someGroupName");
		getHttpClient().post(GroupConstants.inviteCommandPrefix).//
				addParam(GroupConstants.groupIdKey, groupId).//
				addParam(GroupConstants.takeOnSubjectKey, "someSubject").//
				addParam(LoginConstants.softwareFmIdKey, fromSoftwareFmId).//
				addParam(GroupConstants.takeOnFromKey, fromEmail).//
				addParam(GroupConstants.takeOnEmailPattern, "emailPattern: " + GroupConstants.emailMarker + "/" + GroupConstants.groupNameMarker).//
				addParam(GroupConstants.takeOnEmailListKey, "email1@a.b,email2@a.b").//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.serverErrorCode, "class java.lang.IllegalArgumentException/User someNewSoftwareFmId0 is not a member of group someGroupId")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);

		List<Map<String, Object>> actual = Iterables.list(groups.users(groupId, groupCryptoKey));
		List<Map<String, Object>> expected = Collections.emptyList();
		assertEquals(expected, actual);

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
		File groupDirectory = new File(remoteRoot, GroupConstants.groupsGenerator(SoftwareFmConstants.urlPrefix).findUrlFor(Maps.stringObjectMap(GroupConstants.groupIdKey, this.groupId)));
		assertFalse(groupDirectory.exists());
		assertEquals(0, mailerMock.froms.size());
	}

	private void add(IRequestBuilder builder, String key, String value) {
		if (value != null)
			builder.addParam(key, value);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		fromSoftwareFmId = softwareFmIdGenerator.call();
		processCallParameters.signUpChecker.signUp(fromEmail, "someMoniker", "someSalt", "irrelevant", fromSoftwareFmId);
	}

}