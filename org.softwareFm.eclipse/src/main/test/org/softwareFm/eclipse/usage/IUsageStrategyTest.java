package org.softwareFm.eclipse.usage;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.json.Json;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.url.Urls;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.server.processors.AbstractProcessorDatabaseIntegrationTests;

public class IUsageStrategyTest extends AbstractProcessorDatabaseIntegrationTests {

	private IUsageStrategy usageStrategy;
	private File directory;
	private File userFile;
	private File userProjectFile;

	public void testUsingUpdatesProjectData() throws Exception {
		String sessionSalt = makeSalt();
		String crypto = signup("someEmail", sessionSalt, "hash", "someNewSoftwareFmId0");
		usageStrategy.using("someNewSoftwareFmId0", "groupId1", "artifactId1", IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, "")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
		assertTrue(userFile.exists());
		assertTrue(userProjectFile.exists());
		Map<String, Object> projectData = getProjectData(crypto);
		assertEquals(Maps.stringObjectMap("groupId1", Maps.stringObjectMap("artifactId1", Arrays.asList(0l))), projectData);
	}

	public void testMyData() throws Exception {
		String sessionSalt = makeSalt();
		String crypto = signup("someEmail", sessionSalt, "hash", "someNewSoftwareFmId0");
		usageStrategy.using("someNewSoftwareFmId0", "groupId1", "artifactId1", IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, "")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
		usageStrategy.using("someNewSoftwareFmId0", "groupId1", "artifactId2", IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, "")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
		usageStrategy.using("someNewSoftwareFmId0", "groupId2", "artifactId3", IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, "")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);

		Map<String, Object> projectData = getProjectData(crypto);

		List<Long> day = Arrays.asList(0l);
		Object[] expected = new Object[] { "groupId1", Maps.stringObjectMap("artifactId1", day, "artifactId2", day), "groupId2", Maps.stringObjectMap("artifactId3", day) };

		assertEquals(Maps.stringObjectMap(expected), projectData);
		Map<String, Object> actualProjectData = usageStrategy.myProjectData("someNewSoftwareFmId0", crypto);
		assertEquals(Maps.stringObjectMap("someMonth", Maps.stringObjectMap(expected)), actualProjectData);
	}

	private Map<String, Object> getProjectData(String crypto) {
		Map<String, Object> userData = Json.mapFromEncryptedFile(userFile, crypto);
		String projectCrypto = (String) userData.get(SoftwareFmConstants.projectCryptoKey);
		Map<String, Object> projectData = Json.mapFromEncryptedFile(userProjectFile, projectCrypto);
		return projectData;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		usageStrategy = IUsageStrategy.Utils.usage(getServiceExecutor(), getHttpClient(), gitLocal, LoginConstants.userGenerator(), SoftwareFmConstants.projectGenerator());
		directory = new File(remoteRoot, "softwareFm/users/so/me/someNewSoftwareFmId0");
		userFile = new File(directory, CommonConstants.dataFileName);
		userProjectFile = new File(directory, Urls.compose("project", "someMonth"));
	}

}