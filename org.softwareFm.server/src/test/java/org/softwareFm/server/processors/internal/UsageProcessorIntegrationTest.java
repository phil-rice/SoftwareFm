package org.softwareFm.server.processors.internal;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.server.IUser;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.constants.LoginConstants;
import org.softwareFm.server.processors.AbstractProcessorDatabaseIntegrationTests;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;

public class UsageProcessorIntegrationTest extends AbstractProcessorDatabaseIntegrationTests {

	private IUser user;
	private Map<String, Object> userDetails;
	private File userFile;
	private File projectFile;

	public void testSetup() {
		user.saveUserDetails(userDetails, Maps.emptyStringObjectMap());
		assertTrue(userFile.exists());
		Map<String, Object> actualUserDetails = Json.mapFromEncryptedFile(userFile, key);
		assertFalse(actualUserDetails.containsKey(LoginConstants.projectCryptoKey));
	}

	public void testFirstUsageCreatesCryptoInUserAndInitialisesProjectData() throws Exception {
		user.saveUserDetails(userDetails, Maps.emptyStringObjectMap());
		assertTrue(userFile.exists());
		getHttpClient().post("/" + CommonConstants.usagePrefix).//
				addParam(LoginConstants.softwareFmIdKey, "someSoftwareFmId").//
				addParam("g", "someGroupId").//
				addParam("a", "someArtifactId").//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, "")).get(CommonConstants.testTimeOutMs, TimeUnit.SECONDS);
		Map<String, Object> actualUserDetails = Json.mapFromEncryptedFile(userFile, key);
		String projectCryptoKey = (String) actualUserDetails.get(LoginConstants.projectCryptoKey);

		Map<String, Object> actualProjectDetails = Json.mapFromEncryptedFile(projectFile, projectCryptoKey);
		assertEquals(Maps.stringObjectMap("someGroupId", Maps.stringObjectMap("someArtifactId", Arrays.asList(3l))), actualProjectDetails);
	}

	public void testSecondUsageUpdatesProjectDataIfNew() throws Exception {
		user.saveUserDetails(userDetails, Maps.emptyStringObjectMap());
		getHttpClient().post("/" + CommonConstants.usagePrefix).//
				addParam(LoginConstants.softwareFmIdKey, "someSoftwareFmId").//
				addParam("g", "someGroupId").//
				addParam("a", "someArtifactId1").//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, "")).get(CommonConstants.testTimeOutMs, TimeUnit.SECONDS);
		getHttpClient().post("/" + CommonConstants.usagePrefix).//
				addParam(LoginConstants.softwareFmIdKey, "someSoftwareFmId").//
				addParam("g", "someGroupId").//
				addParam("a", "someArtifactId2").//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, "")).get(CommonConstants.testTimeOutMs, TimeUnit.SECONDS);
		Map<String, Object> actualUserDetails = Json.mapFromEncryptedFile(userFile, key);
		String projectCryptoKey = (String) actualUserDetails.get(LoginConstants.projectCryptoKey);

		Map<String, Object> actualProjectDetails = Json.mapFromEncryptedFile(projectFile, projectCryptoKey);
		assertEquals(Maps.stringObjectMap("someGroupId", Maps.stringObjectMap(//
				"someArtifactId1", Arrays.asList(3l),//
				"someArtifactId2", Arrays.asList(3l))), actualProjectDetails);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		user = IProcessCall.Utils.makeUser(remoteOperations);
		userDetails = Maps.stringObjectMap(LoginConstants.softwareFmIdKey, "someSoftwareFmId");
		userFile = new File(remoteRoot, "softwareFm/users/so/me/someSoftwareFmId/" + CommonConstants.dataFileName);
		projectFile = new File(remoteRoot, "softwareFm/users/so/me/someSoftwareFmId/projects/someMonth");
	}
}
