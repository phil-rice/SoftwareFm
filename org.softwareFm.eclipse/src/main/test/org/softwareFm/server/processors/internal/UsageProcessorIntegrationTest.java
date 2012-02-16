/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.processors.internal;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.common.IUser;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.exceptions.WrappedException;
import org.softwareFm.common.json.Json;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.url.Urls;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.server.processors.AbstractProcessorDatabaseIntegrationTests;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.softwareFmServer.SoftwareFmServer;

public class UsageProcessorIntegrationTest extends AbstractProcessorDatabaseIntegrationTests {

	private final String softwareFmId = "someNewSoftwareFmId0";
	private IUser user;
	private File userFile;
	private File projectFile;

	public void testSetup() {
		user.setUserProperty(softwareFmId, userKey, "someProperty", "someValue");

		assertTrue(userFile.exists());
		Map<String, Object> actualUserDetails = Json.mapFromEncryptedFile(userFile, userKey);
		assertFalse(actualUserDetails.containsKey(SoftwareFmConstants.projectCryptoKey));
	}

	public void testFirstUsageCreatesCryptoInUserAndInitialisesProjectData() throws Exception {
		assertFalse(userFile.exists());
		getHttpClient().post("/" + SoftwareFmConstants.usagePrefix).//
				addParam(LoginConstants.softwareFmIdKey, "someNewSoftwareFmId0").// using this so cryptoGenerator/cryptoFnwork
				addParam(SoftwareFmConstants.digest, "digest11").//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, "")).get(CommonConstants.testTimeOutMs, TimeUnit.SECONDS);
		String projectCryptoKey = user.getUserProperty(softwareFmId, userKey, SoftwareFmConstants.projectCryptoKey);
		assertNotNull(projectCryptoKey);
		Map<String, Object> actualProjectDetails = Json.mapFromEncryptedFile(projectFile, projectCryptoKey);
		assertEquals(Maps.stringObjectMap("someGroupId1", Maps.stringObjectMap("someArtifactId1", Arrays.asList(0l))), actualProjectDetails);
	}

	public void testSecondUsageUpdatesProjectDataIfNew() throws Exception {
		checkAdd("digest11",  1, "someGroupId1", Maps.stringObjectMap("someArtifactId1", Arrays.asList(1l)));
		checkAdd("digest11",  1, "someGroupId1", Maps.stringObjectMap("someArtifactId1", Arrays.asList(1l)));
		checkAdd("digest11",  2, "someGroupId1", Maps.stringObjectMap("someArtifactId1", Arrays.asList(1l, 2l)));
		checkAdd("digest12", 3, "someGroupId1", Maps.stringObjectMap("someArtifactId1", Arrays.asList(1l, 2l), "someArtifactId2", Arrays.asList(3l)));
		checkAdd("digest21", 1, //
				"someGroupId1", Maps.stringObjectMap("someArtifactId1", Arrays.asList(1l, 2l), "someArtifactId2", Arrays.asList(3l)),//
				"someGroupId2", Maps.stringObjectMap("someArtifactId1", Arrays.asList(1l)));
	}

	private void checkAdd(String digest, int day, Object... namesAndValues) {
		try {
			thisDay = day;
			getHttpClient().post("/" + SoftwareFmConstants.usagePrefix).//
					addParam(LoginConstants.softwareFmIdKey, "someNewSoftwareFmId0").//
					addParam(SoftwareFmConstants.digest, digest).//
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
		user = IProcessCall.Utils.makeUser(remoteOperations, SoftwareFmServer.makeDefaultProperties(), SoftwareFmConstants.urlPrefix);
		// someNewSoftwareFmId0
		String url = LoginConstants.userGenerator(SoftwareFmConstants.urlPrefix).findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
		userFile = new File(remoteRoot, Urls.compose(url, CommonConstants.dataFileName));
		projectFile = new File(remoteRoot, Urls.compose(url, "project/someMonth"));
	}
}