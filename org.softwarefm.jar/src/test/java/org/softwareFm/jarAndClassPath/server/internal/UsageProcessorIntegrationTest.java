/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.server.internal;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.softwareFm.crowdsource.api.IApiBuilder;
import org.softwareFm.crowdsource.api.IExtraCallProcessorFactory;
import org.softwareFm.crowdsource.api.IExtraReaderWriterConfigurator;
import org.softwareFm.crowdsource.api.ServerConfig;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.server.AbstractProcessorDatabaseIntegrationTests;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.future.Futures;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;
import org.softwareFm.crowdsource.utilities.url.Urls;
import org.softwareFm.jarAndClassPath.api.IGroupArtifactVersionCallback;
import org.softwareFm.jarAndClassPath.api.IJarToGroupArtifactAndVersion;
import org.softwareFm.jarAndClassPath.api.IProjectTimeGetter;
import org.softwareFm.jarAndClassPath.api.SoftwareFmServer;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;
import org.softwareFm.jarAndClassPath.internal.AbstractJarToGroupArtifactVersion;

public class UsageProcessorIntegrationTest extends AbstractProcessorDatabaseIntegrationTests {

	private String softwareFmId;
	private File userFile;
	private File projectFile;

	public void testSetup() {
		assertTrue(userFile.exists());
		Map<String, Object> actualUserDetails = Json.mapFromEncryptedFile(userFile, userKey0);
		assertFalse(actualUserDetails.containsKey(JarAndPathConstants.projectCryptoKey));
	}

	public void testFirstUsageCreatesCryptoInUserAndInitialisesProjectData() throws Exception {
		getHttpClient().post("/" + JarAndPathConstants.usagePrefix).//
				addParam(LoginConstants.softwareFmIdKey, "someNewSoftwareFmId0").// using this so cryptoGenerator/cryptoFnwork
				addParam(JarAndPathConstants.digest, "digest11").//
				execute(IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, "")).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
		String projectCryptoKey = IUserReader.Utils.getUserProperty(readWriter, softwareFmId, userKey0, JarAndPathConstants.projectCryptoKey);
		assertNotNull(projectCryptoKey);
		Map<String, Object> actualProjectDetails = Json.mapFromEncryptedFile(projectFile, projectCryptoKey);
		assertEquals(Maps.stringObjectMap("someGroupId1", Maps.stringObjectMap("someArtifactId1", Arrays.asList(0l))), actualProjectDetails);
	}

	public void testSecondUsageUpdatesProjectDataIfNew() throws Exception {
		checkAdd("digest11", 1, "someGroupId1", Maps.stringObjectMap("someArtifactId1", Arrays.asList(1l)));
		checkAdd("digest11", 1, "someGroupId1", Maps.stringObjectMap("someArtifactId1", Arrays.asList(1l)));
		checkAdd("digest11", 2, "someGroupId1", Maps.stringObjectMap("someArtifactId1", Arrays.asList(1l, 2l)));
		checkAdd("digest12", 3, "someGroupId1", Maps.stringObjectMap("someArtifactId1", Arrays.asList(1l, 2l), "someArtifactId2", Arrays.asList(3l)));
		checkAdd("digest21", 1, //
				"someGroupId1", Maps.stringObjectMap("someArtifactId1", Arrays.asList(1l, 2l), "someArtifactId2", Arrays.asList(3l)),//
				"someGroupId2", Maps.stringObjectMap("someArtifactId1", Arrays.asList(1l)));
	}

	private void checkAdd(String digest, int day, Object... namesAndValues) {
		try {
			thisDay = day;
			getHttpClient().post("/" + JarAndPathConstants.usagePrefix).//
					addParam(LoginConstants.softwareFmIdKey, "someNewSoftwareFmId0").//
					addParam(JarAndPathConstants.digest, digest).//
					execute(IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, "")).get(CommonConstants.testTimeOutMs, TimeUnit.SECONDS);
			Map<String, Object> expected = Maps.stringObjectMap(namesAndValues);
			Map<String, Object> actualUserDetails = Json.mapFromEncryptedFile(userFile, userKey0);
			String projectCryptoKey = (String) actualUserDetails.get(JarAndPathConstants.projectCryptoKey);

			Map<String, Object> actualProjectDetails = Json.mapFromEncryptedFile(projectFile, projectCryptoKey);
			assertEquals(expected, actualProjectDetails);

		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}

	@Override
	protected IExtraCallProcessorFactory getExtraProcessCalls() {
		return SoftwareFmServer.getExtraProcessCalls();
	}

	@Override
	protected IExtraReaderWriterConfigurator<ServerConfig> getServerExtraReaderWriterConfigurator() {
		return new IExtraReaderWriterConfigurator<ServerConfig>() {
			@Override
			public void builder(IApiBuilder builder, ServerConfig serverConfig) {
				SoftwareFmServer.getServerExtraReaderWriterConfigurator(getUrlPrefix()).builder(builder, serverConfig);

				IJarToGroupArtifactAndVersion jarToGroupArtifactAndVersion = getJarToGroupArtifactVersion(JarAndPathConstants.jarUrlGenerator(serverConfig.prefix));
				builder.registerReaderAndWriter(IJarToGroupArtifactAndVersion.class, jarToGroupArtifactAndVersion);

				IProjectTimeGetter projectTimeGetter = new IProjectTimeGetter() {

					@Override
					public String thisMonth() {
						return "someMonth";
					}

					@Override
					public Iterable<String> lastNMonths(int n) {
						fail();
						return null;
					}

					@Override
					public int day() {
						return thisDay;
					}
				};
				builder.registerReaderAndWriter(IProjectTimeGetter.class, projectTimeGetter);
			}
		};
	}

	private IJarToGroupArtifactAndVersion getJarToGroupArtifactVersion(IUrlGenerator jarUrlGenerator) {
		return new AbstractJarToGroupArtifactVersion(jarUrlGenerator) {
			private final Map<String, Map<String, Object>> jarMap = Maps.makeMap(//
					"digest11", make("someGroupId1", "someArtifactId1", "someVersion1"),//
					"digest12", make("someGroupId1", "someArtifactId2", "someVersion1"),//
					"digest21", make("someGroupId2", "someArtifactId1", "someVersion1"),//
					"digest22", make("someGroupId2", "someArtifactId2", "someVersion1"),//
					"digest23", make("someGroupId2", "someArtifactId3", "someVersion1"));

			@Override
			protected Future<?> processMapFrom(IFileDescription fileDescription, IGroupArtifactVersionCallback callback) {
				String url = fileDescription.url();
				String digest = Strings.lastSegment(url, "/");
				replyTo(callback, jarMap.get(digest));
				return Futures.doneFuture(null);
			}

			private Map<String, Object> make(String groupId, String artifactId, String version) {
				return Maps.makeMap(JarAndPathConstants.groupIdKey, groupId, JarAndPathConstants.artifactIdKey, artifactId, JarAndPathConstants.version, version);
			}
		};
	}

	@Override
	protected Map<String, Callable<Object>> getDefaultUserValues() {
		return SoftwareFmServer.getDefaultUserValues();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		truncateUsersTable();
		softwareFmId = createUser();

		String url = LoginConstants.userGenerator(getUrlPrefix()).findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
		userFile = new File(remoteRoot, Urls.compose(url, CommonConstants.dataFileName));
		projectFile = new File(remoteRoot, Urls.compose(url, "project/someMonth"));

	}
}