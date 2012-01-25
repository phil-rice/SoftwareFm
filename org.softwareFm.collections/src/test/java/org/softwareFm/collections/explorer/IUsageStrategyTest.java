package org.softwareFm.collections.explorer;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.AbstractProcessorDatabaseIntegrationTests;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.url.Urls;

public class IUsageStrategyTest extends AbstractProcessorDatabaseIntegrationTests {

	private IUsageStrategy usageStrategy;
	private File directory;
	private File userFile;
	private File userProjectFile;

	public void test() throws Exception {
		String sessionSalt = makeSalt();
		String crypto = signup("someEmail", sessionSalt, "hash", "someNewSoftwareFmId0");
		usageStrategy.using("someNewSoftwareFmId0", "groupId1", "artifactId1", IResponseCallback.Utils.checkCallback(ServerConstants.okStatusCode, "")).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
		assertTrue(userFile.exists());
		assertTrue(userProjectFile.exists());
		Map<String, Object> userData = Json.mapFromEncryptedFile(userFile, crypto);
		String projectCrypto = (String) userData.get(ServerConstants.projectCryptoKey);
		Map<String, Object> projectData = Json.mapFromEncryptedFile(userProjectFile, projectCrypto);
		assertEquals(Maps.stringObjectMap("groupId1", Maps.stringObjectMap("artifactId1", Arrays.asList(3l))), projectData);
		
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		usageStrategy = IUsageStrategy.Utils.usage(getHttpClient(), "g", "a");
		directory = new File(remoteRoot, "users/so/me/someNewSoftwareFmId0");
		userFile = new File(directory, ServerConstants.dataFileName);
		userProjectFile = new File(directory, Urls.compose("projects", "someMonth"));
	}

}
