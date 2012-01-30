package org.softwareFm.server.processors.internal;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.softwareFm.crowdsourced.softwarefm.SoftwareFmConstants;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.server.IUser;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.constants.LoginConstants;
import org.softwareFm.server.processors.AbstractProcessorDatabaseIntegrationTests;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;

public class UsageProcessorIntegrationTest extends AbstractProcessorDatabaseIntegrationTests {

	private IUser user;
	private Map<String, Object> userDetails;
	private File userFile;
	private File projectFile;

	public void testSetup() {
		user.setUserProperty(userDetails, userKey, "someProperty", "someValue");

		assertTrue(userFile.exists());
		Map<String, Object> actualUserDetails = Json.mapFromEncryptedFile(userFile, userKey);
		assertFalse(actualUserDetails.containsKey(SoftwareFmConstants.projectCryptoKey));
	}

	public void testFirstUsageCreatesCryptoInUserAndInitialisesProjectData() throws Exception {
		assertFalse(userFile.exists());
		getHttpClient().post("/" + SoftwareFmConstants.usagePrefix).//
				addParam(LoginConstants.softwareFmIdKey, "someSoftwareFmId").//
				addParam(LoginConstants.emailKey, "someEmail").//
				addParam(SoftwareFmConstants.groupIdKey, "someGroupId").//
				addParam(SoftwareFmConstants.artifactIdKey, "someArtifactId").//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, "")).get(CommonConstants.testTimeOutMs, TimeUnit.SECONDS);
		String projectCryptoKey = user.getUserProperty(userDetails, userKey, SoftwareFmConstants.projectCryptoKey);
		assertNotNull(projectCryptoKey);
		Map<String, Object> actualProjectDetails = Json.mapFromEncryptedFile(projectFile, projectCryptoKey);
		assertEquals(Maps.stringObjectMap("someGroupId", Maps.stringObjectMap("someArtifactId", Arrays.asList(0l))), actualProjectDetails);
	}

	public void testSecondUsageUpdatesProjectDataIfNew() throws Exception {
		checkAdd("someGroupId1", "someArtifactId1", 1, "someGroupId1", Maps.stringObjectMap("someArtifactId1", Arrays.asList(1l)));
		checkAdd("someGroupId1", "someArtifactId1", 1, "someGroupId1", Maps.stringObjectMap("someArtifactId1", Arrays.asList(1l)));
		checkAdd("someGroupId1", "someArtifactId1", 2, "someGroupId1", Maps.stringObjectMap("someArtifactId1", Arrays.asList(1l, 2l)));
		checkAdd("someGroupId1", "someArtifactId2", 3, "someGroupId1", Maps.stringObjectMap("someArtifactId1", Arrays.asList(1l, 2l), "someArtifactId2", Arrays.asList(3l)));
		checkAdd("someGroupId2", "someArtifactId1", 1, //
				"someGroupId1", Maps.stringObjectMap("someArtifactId1", Arrays.asList(1l, 2l), "someArtifactId2", Arrays.asList(3l)),//
				"someGroupId2", Maps.stringObjectMap("someArtifactId1", Arrays.asList(1l)));
	}

	private void checkAdd(String groupId, String artifactId, int day, Object... namesAndValues) {
		try {
			thisDay = day;
			getHttpClient().post("/" + SoftwareFmConstants.usagePrefix).//
					addParam(LoginConstants.softwareFmIdKey, "someSoftwareFmId").//
					addParam(LoginConstants.emailKey, "someEmail").//
					addParam(SoftwareFmConstants.groupIdKey, groupId).//
					addParam(SoftwareFmConstants.artifactIdKey, artifactId).//
					execute(IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, "")).get(CommonConstants.testTimeOutMs, TimeUnit.SECONDS);
			Map<String, Object> expected = Maps.stringObjectMap(namesAndValues);
			Map<String, Object> actualUserDetails = Json.mapFromEncryptedFile(userFile, userKey);
			String projectCryptoKey = (String) actualUserDetails.get(SoftwareFmConstants.projectCryptoKey);

			Map<String, Object> actualProjectDetails = Json.mapFromEncryptedFile(projectFile, projectCryptoKey);
			assertEquals(expected, actualProjectDetails);

		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		user = IProcessCall.Utils.makeUser(remoteOperations);
		userDetails = Maps.stringObjectMap(LoginConstants.softwareFmIdKey, "someSoftwareFmId");
		userFile = new File(remoteRoot, "softwareFm/users/so/me/someSoftwareFmId/" + CommonConstants.dataFileName);
		projectFile = new File(remoteRoot, "softwareFm/users/so/me/someSoftwareFmId/project/someMonth");
	}
}
