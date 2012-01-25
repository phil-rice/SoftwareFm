package org.softwareFm.server.processors.internal;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.user.IUser;
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
		assertFalse(actualUserDetails.containsKey(ServerConstants.projectCryptoKey));
	}

	public void testFirstUsageCreatesCryptoInUserAndInitialisesProjectData() throws Exception {
		user.saveUserDetails(userDetails, Maps.emptyStringObjectMap());
		assertTrue(userFile.exists());
		getHttpClient().post("/" + ServerConstants.usagePrefix).//
				addParam(ServerConstants.softwareFmIdKey, "someSoftwareFmId").//
				addParam("g", "someGroupId").//
				addParam("a", "someArtifactId").//
				execute(IResponseCallback.Utils.checkCallback(ServerConstants.okStatusCode, "")).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
		Map<String, Object> actualUserDetails = Json.mapFromEncryptedFile(userFile, key);
		String projectCryptoKey = (String) actualUserDetails.get(ServerConstants.projectCryptoKey);

		Map<String, Object> actualProjectDetails = Json.mapFromEncryptedFile(projectFile, projectCryptoKey);
		assertEquals(Maps.stringObjectMap("someGroupId", Maps.stringObjectMap("someArtifactId", Arrays.asList(3l))), actualProjectDetails);
	}

	public void testSecondUsageUpdatesProjectDataIfNew() throws Exception {
		user.saveUserDetails(userDetails, Maps.emptyStringObjectMap());
		getHttpClient().post("/" + ServerConstants.usagePrefix).//
				addParam(ServerConstants.softwareFmIdKey, "someSoftwareFmId").//
				addParam("g", "someGroupId").//
				addParam("a", "someArtifactId1").//
				execute(IResponseCallback.Utils.checkCallback(ServerConstants.okStatusCode, "")).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
		getHttpClient().post("/" + ServerConstants.usagePrefix).//
				addParam(ServerConstants.softwareFmIdKey, "someSoftwareFmId").//
				addParam("g", "someGroupId").//
				addParam("a", "someArtifactId2").//
				execute(IResponseCallback.Utils.checkCallback(ServerConstants.okStatusCode, "")).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
		Map<String, Object> actualUserDetails = Json.mapFromEncryptedFile(userFile, key);
		String projectCryptoKey = (String) actualUserDetails.get(ServerConstants.projectCryptoKey);

		Map<String, Object> actualProjectDetails = Json.mapFromEncryptedFile(projectFile, projectCryptoKey);
		assertEquals(Maps.stringObjectMap("someGroupId", Maps.stringObjectMap(//
				"someArtifactId1", Arrays.asList(3l),//
				"someArtifactId2", Arrays.asList(3l))), actualProjectDetails);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		user = IProcessCall.Utils.makeUser(gitServer, cryptoFn);
		userDetails = Maps.stringObjectMap(ServerConstants.softwareFmIdKey, "someSoftwareFmId");
		userFile = new File(remoteRoot, "so/me/someSoftwareFmId/" + ServerConstants.dataFileName);
		projectFile = new File(remoteRoot, "so/me/someSoftwareFmId/projects/someMonth");
	}
}
