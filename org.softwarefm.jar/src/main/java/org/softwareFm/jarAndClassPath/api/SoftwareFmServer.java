/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.api;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.xml.DOMConfigurator;
import org.softwareFm.crowdsource.api.IApiBuilder;
import org.softwareFm.crowdsource.api.ICrowdSourceReadWriteApi;
import org.softwareFm.crowdsource.api.ICrowdSourcedServer;
import org.softwareFm.crowdsource.api.ICrowdSourcesApi;
import org.softwareFm.crowdsource.api.ICryptoGenerators;
import org.softwareFm.crowdsource.api.IExtraCallProcessorFactory;
import org.softwareFm.crowdsource.api.IExtraReaderWriterConfigurator;
import org.softwareFm.crowdsource.api.IIdAndSaltGenerator;
import org.softwareFm.crowdsource.api.ITakeOnEnrichmentProvider;
import org.softwareFm.crowdsource.api.IUserCryptoAccess;
import org.softwareFm.crowdsource.api.ServerConfig;
import org.softwareFm.crowdsource.api.server.ICallProcessor;
import org.softwareFm.crowdsource.api.server.IMailer;
import org.softwareFm.crowdsource.api.server.IUsage;
import org.softwareFm.crowdsource.constants.CommentConstants;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.processors.AbstractLoginDataAccessor;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;
import org.softwareFm.jarAndClassPath.server.internal.GenerateGroupUsageProcessor;
import org.softwareFm.jarAndClassPath.server.internal.GenerateUsageProjectGenerator;
import org.softwareFm.jarAndClassPath.server.internal.JarToGroupArtifactVersionOnServer;
import org.softwareFm.jarAndClassPath.server.internal.ProjectForServer;
import org.softwareFm.jarAndClassPath.server.internal.UsageProcessor;
import org.softwareFm.jarAndClassPath.server.internal.UsageReaderForServer;

public class SoftwareFmServer {

	public static void main(String[] args) {
		DOMConfigurator.configure(System.getProperty("user.home") + "/log4j.xml");
		int port = ICrowdSourcedServer.Utils.port(args);

		makeSoftwareFmServer(port);
	}

	public static void makeSoftwareFmServer(int port) {
		ServerConfig serverConfig = getServerConfig(port);
		ICrowdSourcesApi api = ICrowdSourcesApi.Utils.forServer(serverConfig);
		api.getServer();
	}

	public static ServerConfig getServerConfig(int port) {
		BasicDataSource dataSource = AbstractLoginDataAccessor.defaultDataSource();
		File root = ICrowdSourcedServer.Utils.makeSfmRoot();
		IIdAndSaltGenerator idAndSaltGenerator = IIdAndSaltGenerator.Utils.uuidGenerators();
		IUserCryptoAccess userCryptoAccess = IUserCryptoAccess.Utils.database(dataSource, idAndSaltGenerator);
		ITakeOnEnrichmentProvider takeOnEnrichment = getTakeOnEnrichment();
		IExtraCallProcessorFactory extraCallProcessors = IExtraCallProcessorFactory.Utils.noExtraCalls();
		IUsage usage = IUsage.Utils.defaultUsage();
		ICryptoGenerators cryptoGenerators = ICryptoGenerators.Utils.cryptoGenerators();
		final String prefix = JarAndPathConstants.urlPrefix;
		Map<String, Callable<Object>> defaultUserValues = getDefaultUserValues();
		Map<String, Callable<Object>> defaultGroupValues = getDefaultGroupValues();
		ICallback<Throwable> errorHandler = ICallback.Utils.rethrow();
		IMailer mailer = IMailer.Utils.email("localhost", null, null);
		Callable<Long> timeGetter = Callables.time();
		IExtraReaderWriterConfigurator<ServerConfig> extraReadWriterConfigurator = getServerExtraReaderWriterConfigurator(prefix);
		ServerConfig serverConfig = new ServerConfig(port, 1000, root, dataSource, takeOnEnrichment, extraCallProcessors, usage, idAndSaltGenerator, cryptoGenerators, userCryptoAccess, prefix, defaultUserValues, defaultGroupValues, errorHandler, mailer, timeGetter, extraReadWriterConfigurator);//
		return serverConfig;
	}

	public static IExtraReaderWriterConfigurator<ServerConfig> getServerExtraReaderWriterConfigurator(final String prefix) {
		return new IExtraReaderWriterConfigurator<ServerConfig>() {
			@Override
			public void builder(IApiBuilder builder, ServerConfig serverConfig) {
				IUsageReader usageReader = new UsageReaderForServer(builder, serverConfig.userUrlGenerator);
				builder.registerReader(IUsageReader.class, usageReader);

				IGenerateUsageReportGenerator generator = new GenerateUsageProjectGenerator(builder);
				builder.registerReadWriter(IGenerateUsageReportGenerator.class, generator);

				ProjectForServer project = new ProjectForServer(builder, serverConfig.userCryptoAccess, serverConfig.userUrlGenerator);
				builder.registerReaderAndWriter(IProject.class, project);

				IJarToGroupArtifactAndVersion jarToGroupArtifactAndVersion = new JarToGroupArtifactVersionOnServer(builder, JarAndPathConstants.jarUrlGenerator(prefix));
				builder.registerReaderAndWriter(IJarToGroupArtifactAndVersion.class, jarToGroupArtifactAndVersion);

				IProjectTimeGetter projectTimeGetter = IProjectTimeGetter.Utils.timeGetter();
				builder.registerReaderAndWriter(IProjectTimeGetter.class, projectTimeGetter);
			}
		};
	}

	public static ITakeOnEnrichmentProvider getTakeOnEnrichment() {
		return ITakeOnEnrichmentProvider.Utils.enrichmentWithUserProperty(JarAndPathConstants.projectCryptoKey);
	}

	public static Map<String, Callable<Object>> getDefaultUserValues() {
		return Maps.makeMap(CommentConstants.commentCryptoKey, Callables.makeCryptoKey(), //
				GroupConstants.membershipCryptoKey, Callables.makeCryptoKey(),//
				GroupConstants.groupCryptoKey, Callables.makeCryptoKey(), //
				JarAndPathConstants.projectCryptoKey, Callables.makeCryptoKey());
	}

	public static Map<String, Callable<Object>> getDefaultGroupValues() {
		return Maps.makeMap();
	}

	public static IExtraCallProcessorFactory getExtraProcessCalls() {
		return new IExtraCallProcessorFactory() {
			@Override
			public ICallProcessor[] makeExtraCalls(ICrowdSourceReadWriteApi api, ServerConfig serverConfig) {
				return new ICallProcessor[] { //
				new UsageProcessor(api),//
						new GenerateGroupUsageProcessor(api) };
			}
		};
	}

}