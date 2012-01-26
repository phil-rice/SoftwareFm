package org.softwareFm.collections.explorer;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.server.IGitServer;
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

	public void testUsingUpdatesProjectData() throws Exception {
		String sessionSalt = makeSalt();
		String crypto = signup("someEmail", sessionSalt, "hash", "someNewSoftwareFmId0");
		usageStrategy.using("someNewSoftwareFmId0", "groupId1", "artifactId1", IResponseCallback.Utils.checkCallback(ServerConstants.okStatusCode, "")).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
		assertTrue(userFile.exists());
		assertTrue(userProjectFile.exists());
		Map<String, Object> projectData = getProjectData(crypto);
		assertEquals(Maps.stringObjectMap("groupId1", Maps.stringObjectMap("artifactId1", Arrays.asList(3l))), projectData);
	}

	public void testMyData() throws Exception {
		String sessionSalt = makeSalt();
		String crypto = signup("someEmail", sessionSalt, "hash", "someNewSoftwareFmId0");
		usageStrategy.using("someNewSoftwareFmId0", "groupId1", "artifactId1", IResponseCallback.Utils.checkCallback(ServerConstants.okStatusCode, "")).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
		usageStrategy.using("someNewSoftwareFmId0", "groupId1", "artifactId2", IResponseCallback.Utils.checkCallback(ServerConstants.okStatusCode, "")).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
		usageStrategy.using("someNewSoftwareFmId0", "groupId2", "artifactId3", IResponseCallback.Utils.checkCallback(ServerConstants.okStatusCode, "")).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);

		Map<String, Object> projectData = getProjectData(crypto);

		List<Long> day = Arrays.asList(3l);
		Object[] expected = new Object[] { "groupId1", Maps.stringObjectMap("artifactId1", day, "artifactId2", day), "groupId2", Maps.stringObjectMap("artifactId3", day) };

		assertEquals(Maps.stringObjectMap(expected), projectData);
		Map<String, Object> actualProjectData = usageStrategy.myProjectData("someNewSoftwareFmId0", crypto);
		assertEquals(Maps.stringObjectMap("someMonth", Maps.stringObjectMap(expected)), actualProjectData);
	}

	private Map<String, Object> getProjectData(String crypto) {
		Map<String, Object> userData = Json.mapFromEncryptedFile(userFile, crypto);
		String projectCrypto = (String) userData.get(ServerConstants.projectCryptoKey);
		Map<String, Object> projectData = Json.mapFromEncryptedFile(userProjectFile, projectCrypto);
		return projectData;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		IGitServer localGitServer = IGitServer.Utils.gitServer(localRoot, remoteRoot.getAbsolutePath());
		usageStrategy = IUsageStrategy.Utils.usage(getServiceExecutor(), getHttpClient(), localGitServer, ServerConstants.userGenerator(), ServerConstants.projectGenerator(), "g", "a");
		directory = new File(remoteRoot, "softwareFm/users/so/me/someNewSoftwareFmId0");
		userFile = new File(directory, ServerConstants.dataFileName);
		userProjectFile = new File(directory, Urls.compose("projects", "someMonth"));
	}

}
