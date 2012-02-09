package org.softwareFm.server.processors.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.common.collections.Iterables;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.server.processors.AbstractProcessorDatabaseIntegrationTests;

public class TakeOnGroupProcessorTest extends AbstractProcessorDatabaseIntegrationTests {

	@SuppressWarnings("unchecked")
	public void testTakeOnGroup() throws Exception {
		getHttpClient().post(GroupConstants.takeOnCommandPrefix).//
				addParam(GroupConstants.groupNameKey, "someNewGroupName").//
				addParam(GroupConstants.takeOnEmailPattern, "emailPattern{0}").//
				addParam(GroupConstants.takeOnEmailListKey, "email1,email2,email3").//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, "")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
		List<Map<String, Object>> actual = Iterables.list(groups.users(groupId, groupCryptoKey));
		List<Map<String, Object>> expected = Arrays.asList(//
				Maps.stringObjectMap(LoginConstants.softwareFmIdKey, "someNewSoftwareFmId0", SoftwareFmConstants.projectCryptoKey, projectCryptoKey1, LoginConstants.emailKey, "email1", GroupConstants.userStatusInGroup, GroupConstants.invitedStatus),//
				Maps.stringObjectMap(LoginConstants.softwareFmIdKey, "someNewSoftwareFmId1", SoftwareFmConstants.projectCryptoKey, projectCryptoKey2, LoginConstants.emailKey, "email2", GroupConstants.userStatusInGroup, GroupConstants.invitedStatus),//
				Maps.stringObjectMap(LoginConstants.softwareFmIdKey, "someNewSoftwareFmId2", SoftwareFmConstants.projectCryptoKey, projectCryptoKey3, LoginConstants.emailKey, "email3", GroupConstants.userStatusInGroup, GroupConstants.invitedStatus));
		assertEquals(expected, actual);
		assertEquals("someNewGroupName", groups.getGroupProperty(groupId, groupCryptoKey, GroupConstants.groupNameKey));
	}

}
