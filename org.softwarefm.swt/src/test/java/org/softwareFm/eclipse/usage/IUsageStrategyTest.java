/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.usage;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.softwareFm.crowdsource.api.server.AbstractProcessorDatabaseIntegrationTests;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.url.Urls;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;

public class IUsageStrategyTest extends AbstractProcessorDatabaseIntegrationTests {

	private IUsageStrategy usageStrategy;
	private File directory;
	private File userFile;
	private File userProjectFile;
	private final String email = "a@b.com";

	public void testUsingUpdatesProjectData() throws Exception {
		String sessionSalt = makeSalt();
		String crypto = signup(email, sessionSalt, "someMoniker", "hash", "someNewSoftwareFmId0");
		usageStrategy.using("someNewSoftwareFmId0", "digest11", IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, "")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
		assertTrue(userFile.exists());
		assertTrue(userProjectFile.exists());
		Map<String, Object> projectData = getProjectData(crypto);
		assertEquals(Maps.stringObjectMap("someGroupId1", Maps.stringObjectMap("someArtifactId1", Arrays.asList(0l))), projectData);
	}

	public void testMyData() throws Exception {
		String sessionSalt = makeSalt();
		String crypto = signup(email, sessionSalt, "someMoniker", "hash", "someNewSoftwareFmId0");
		usageStrategy.using("someNewSoftwareFmId0", "digest11", IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, "")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
		usageStrategy.using("someNewSoftwareFmId0", "digest12", IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, "")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
		usageStrategy.using("someNewSoftwareFmId0", "digest23", IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, "")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);

		Map<String, Object> projectData = getProjectData(crypto);

		List<Long> day = Arrays.asList(0l);
		Object[] expected = new Object[] { "someGroupId1", Maps.stringObjectMap("someArtifactId1", day, "someArtifactId2", day), "someGroupId2", Maps.stringObjectMap("someArtifactId3", day) };

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
		usageStrategy = IUsageStrategy.Utils.usage(getServiceExecutor(), getHttpClient(), gitLocal, LoginConstants.userGenerator(SoftwareFmConstants.urlPrefix));
		directory = new File(remoteRoot, "softwareFm/users/so/me/someNewSoftwareFmId0");
		userFile = new File(directory, CommonConstants.dataFileName);
		userProjectFile = new File(directory, Urls.compose("project", "someMonth"));
	}

}