/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.softwareFm.crowdsource.api.MailerMock;
import org.softwareFm.crowdsource.api.server.AbstractProcessorDatabaseIntegrationTests;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.api.user.IUserMembershipReader;
import org.softwareFm.crowdsource.httpClient.IRequestBuilder;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.httpClient.internal.MemoryResponseCallback;
import org.softwareFm.crowdsource.utilities.collections.Iterables;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction2;
import org.softwareFm.crowdsource.utilities.maps.Maps;

public class TakeOnGroupProcessorTest extends AbstractProcessorDatabaseIntegrationTests {

	private final String fromEmail = "from@some.email";
	final String expectedGroupId = "groupId0";
	private String fromSoftwareFmId;
	protected int count;

	@SuppressWarnings("unchecked")
	public void testTakeOnGroup() throws Exception {
	
		getHttpClient().post(GroupConstants.takeOnCommandPrefix).//
				addParam(GroupConstants.groupNameKey, "someNewGroupName").//
				addParam(GroupConstants.takeOnSubjectKey, "someSubject").//
				addParam(LoginConstants.softwareFmIdKey, fromSoftwareFmId).//
				addParam(GroupConstants.takeOnFromKey, fromEmail).//
				addParam(GroupConstants.takeOnEmailPattern, "emailPattern: " + GroupConstants.emailMarker + "/" + GroupConstants.groupNameMarker).//
				addParam(GroupConstants.takeOnEmailListKey, "email1@a.b,email2@a.b").//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, expectedGroupId)).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);

		api.makeContainer().accessUserMembershipReader(new IFunction2<IGroupsReader, IUserMembershipReader, Void>() {
			@Override
			public Void apply(IGroupsReader groups, IUserMembershipReader second) throws Exception {
				List<Map<String, Object>> actual = Iterables.list(groups.users(expectedGroupId, groupCryptoKey0));
				List<Map<String, Object>> expected = Arrays.asList(//
						Maps.stringObjectMap(LoginConstants.softwareFmIdKey, fromSoftwareFmId, "with", "enrich_0", LoginConstants.emailKey, fromEmail, GroupConstants.membershipStatusKey, GroupConstants.adminStatus),//
						Maps.stringObjectMap(LoginConstants.softwareFmIdKey, "someNewSoftwareFmId1", "with", "enrich_1", LoginConstants.emailKey, "email1@a.b", GroupConstants.membershipStatusKey, GroupConstants.invitedStatus),//
						Maps.stringObjectMap(LoginConstants.softwareFmIdKey, "someNewSoftwareFmId2", "with", "enrich_2", LoginConstants.emailKey, "email2@a.b", GroupConstants.membershipStatusKey, GroupConstants.invitedStatus));
				assertEquals(expected, actual);
				assertEquals("someNewGroupName", groups.getGroupProperty(expectedGroupId, groupCryptoKey0, GroupConstants.groupNameKey));
				return null;
			}
		});
		MailerMock mailerMock = getMailer();
		assertEquals(Lists.times(2, fromEmail), mailerMock.froms);
		assertEquals(Arrays.asList("email1@a.b", "email2@a.b"), mailerMock.tos);
		assertEquals(Lists.times(2, "someSubject"), mailerMock.subjects);
		assertEquals(Lists.times(2, "emailPattern: from@some.email/someNewGroupName"), mailerMock.messages);
	}

	public void testThrowsExceptionsAndAddNoUsersIfPropertiesNotFullySet() throws Exception {
		checkCannotTakeOn(null, "someSubject", "someFrom", "emailPattern: $email$", "email1@a.b,email2@a.b,email3@a.b");
		checkCannotTakeOn("someNewGroupName", null, "someFrom", "emailPattern: $email$", "email1@a.b,email2@a.b,email3@a.b");
		checkCannotTakeOn("someNewGroupName", "someSubject", null, "emailPattern: $email$", "email1@a.b,email2@a.b,email3@a.b");
		checkCannotTakeOn("someNewGroupName", "someSubject", "someFrom", null, "email1@a.b,email2@a.b,email3@a.b");
		checkCannotTakeOn("someNewGroupName", "someSubject", "someFrom", "emailPattern: $email$", null);
		checkCannotTakeOn("someNewGroupName", "someSubject", "someFrom", "emailPattern: $email$", "email1");
		checkCannotTakeOn("someNewGroupName", "someSubject", "someFrom", "emailPattern: $email$", "email1,email2@a.b");

		checkCannotTakeOn("someNewGroupName", "someSubject", null, "emailPattern: $email$", "email1@a.b,email1@a.b,email3@a.b");// duplicate
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

	public void testThrowsExceptionIfOneOfTheEmailListIsNotAnEmail() throws InterruptedException, ExecutionException, TimeoutException {
		getHttpClient().post(GroupConstants.takeOnCommandPrefix).//
				addParam(GroupConstants.groupNameKey, "someNewGroupName").//
				addParam(GroupConstants.takeOnSubjectKey, "someSubject").//
				addParam(LoginConstants.softwareFmIdKey, fromSoftwareFmId).//
				addParam(GroupConstants.takeOnFromKey, "wrongEmail").//
				addParam(GroupConstants.takeOnEmailPattern, "emailPattern: " + GroupConstants.emailMarker + "/" + GroupConstants.groupNameMarker).//
				addParam(GroupConstants.takeOnEmailListKey, "email1@a.b,email2@a.b,notAnEmail").//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.serverErrorCode, "class java.lang.IllegalArgumentException/Invalid email wrongEmail")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
	}

	public void testThrowsExceptionIfEmailAndSoftwareFmNotForSameUser() throws InterruptedException, ExecutionException, TimeoutException {
		getHttpClient().post(GroupConstants.takeOnCommandPrefix).//
				addParam(GroupConstants.groupNameKey, "someNewGroupName").//
				addParam(GroupConstants.takeOnSubjectKey, "someSubject").//
				addParam(LoginConstants.softwareFmIdKey, fromSoftwareFmId).//
				addParam(GroupConstants.takeOnFromKey, "wrongEmail@a.b").//
				addParam(GroupConstants.takeOnEmailPattern, "emailPattern: " + GroupConstants.emailMarker + "/" + GroupConstants.groupNameMarker).//
				addParam(GroupConstants.takeOnEmailListKey, "email1@a.b,email2@a.b").//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.serverErrorCode, "class java.lang.IllegalArgumentException/Email / SoftwareFm mismatch. Email wrongEmail@a.b Expected null SoftwareFmId someNewSoftwareFmId0")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
	}

	protected void checkCannotTakeOn(String groupName, String subject, String from, String emailPattern, String emailList) throws InterruptedException, ExecutionException, TimeoutException {
		IRequestBuilder builder = getHttpClient().post(GroupConstants.takeOnCommandPrefix);
		add(builder, GroupConstants.groupNameKey, groupName);//
		add(builder, GroupConstants.takeOnSubjectKey, subject);//
		add(builder, GroupConstants.takeOnFromKey, from);//
		add(builder, GroupConstants.takeOnEmailPattern, emailPattern);//
		add(builder, GroupConstants.takeOnEmailListKey, emailList);//
		MemoryResponseCallback memoryCallback = IResponseCallback.Utils.memoryCallback();
		builder.execute(memoryCallback).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
		memoryCallback.assertCalledOnce();
		assertEquals(500, memoryCallback.response.statusCode());
		assertTrue(memoryCallback.response.asString(), memoryCallback.response.asString().startsWith("class java.lang.IllegalAr"));
		if (groupName != null) {
			File groupDirectory = new File(remoteRoot, GroupConstants.groupsGenerator(getUrlPrefix()).findUrlFor(Maps.stringObjectMap(GroupConstants.groupIdKey, expectedGroupId)));
			assertFalse(groupDirectory.exists());
		}
		assertEquals(0, getMailer().froms.size());
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