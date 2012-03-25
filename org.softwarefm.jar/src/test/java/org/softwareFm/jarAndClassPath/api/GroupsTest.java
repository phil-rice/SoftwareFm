/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.api;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.crowdsource.api.ApiTest;
import org.softwareFm.crowdsource.api.IApiBuilder;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.IExtraReaderWriterConfigurator;
import org.softwareFm.crowdsource.api.ITakeOnEnrichmentProvider;
import org.softwareFm.crowdsource.api.ServerConfig;
import org.softwareFm.crowdsource.utilities.collections.Files;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.softwareFm.crowdsource.utilities.url.Urls;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;
import org.softwareFm.jarAndClassPath.server.internal.GenerateUsageProjectGenerator;
import org.softwareFm.jarAndClassPath.server.internal.ProjectForServer;
import org.softwareFm.jarAndClassPath.server.internal.UsageReaderForServer;

abstract public class GroupsTest extends ApiTest {
	protected final String groupCrypto = Crypto.makeKey();

	protected final String groupId = "groupId";

	protected String sfmId1;
	protected String sfmId2;

	protected IContainer serverContainer;

	protected void saveProjectData(String softwareFmId, String projectCrypto, String month, ProjectMock project) {
		String url = Urls.compose(getServerConfig().userUrlGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId)), JarAndPathConstants.projectDirectoryName);
		File projectDirectory = new File(remoteRoot, url);
		Map<String, Map<String, List<Integer>>> projectDetails = project.getProjectDetails(softwareFmId, projectCrypto, month);
		File file = new File(projectDirectory, month);
		Files.makeDirectoryForFile(file);
		Files.setText(file, Crypto.aesEncrypt(projectCrypto, Json.toString(projectDetails)));
	}

	@Override
	protected IExtraReaderWriterConfigurator<ServerConfig> getServerExtraReaderWriterConfigurator() {
		return new IExtraReaderWriterConfigurator<ServerConfig>() {
			@Override
			public void builder(IApiBuilder builder, ServerConfig apiConfig) {
				builder.registerReaderAndWriter(IGenerateUsageReportGenerator.class, new GenerateUsageProjectGenerator(builder));
				builder.registerReaderAndWriter(IUsageReader.class, new UsageReaderForServer(builder, apiConfig.userUrlGenerator));
				builder.registerReaderAndWriter(IProject.class, new ProjectForServer(builder, getUserCryptoAccess(), apiConfig.userUrlGenerator));
			}
		};
	}

	@Override
	protected Map<String, Callable<Object>> getDefaultUserValues() {
		return Maps.with(super.getDefaultUserValues(), JarAndPathConstants.projectCryptoKey, Callables.makeCryptoKey());
	}

	@Override
	protected ITakeOnEnrichmentProvider getTakeOnEnrichment() {
		return ITakeOnEnrichmentProvider.Utils.enrichmentWithUserProperty(JarAndPathConstants.projectCryptoKey);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		truncateUsersTable();
		sfmId1 = createUser();
		sfmId2 = createUser();
		serverContainer = getServerContainer();
	}
}