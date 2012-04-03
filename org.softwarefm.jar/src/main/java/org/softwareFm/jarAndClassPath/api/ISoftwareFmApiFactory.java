package org.softwareFm.jarAndClassPath.api;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.dbcp.BasicDataSource;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.IContainerBuilder;
import org.softwareFm.crowdsource.api.ICrowdSourcedApi;
import org.softwareFm.crowdsource.api.ICrowdSourcedServer;
import org.softwareFm.crowdsource.api.ICryptoGenerators;
import org.softwareFm.crowdsource.api.IExtraCallProcessorFactory;
import org.softwareFm.crowdsource.api.IExtraReaderWriterConfigurator;
import org.softwareFm.crowdsource.api.IIdAndSaltGenerator;
import org.softwareFm.crowdsource.api.ITakeOnEnrichmentProvider;
import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
import org.softwareFm.crowdsource.api.IUserCryptoAccess;
import org.softwareFm.crowdsource.api.LocalConfig;
import org.softwareFm.crowdsource.api.ServerConfig;
import org.softwareFm.crowdsource.api.server.ICallProcessor;
import org.softwareFm.crowdsource.api.server.IMailer;
import org.softwareFm.crowdsource.api.server.IUsage;
import org.softwareFm.crowdsource.constants.CommentConstants;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.processors.AbstractLoginDataAccessor;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.softwareFm.crowdsource.utilities.transaction.ITransactionManager;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;
import org.softwareFm.jarAndClassPath.server.internal.GenerateGroupUsageProcessor;
import org.softwareFm.jarAndClassPath.server.internal.GenerateUsageProjectGenerator;
import org.softwareFm.jarAndClassPath.server.internal.JarToGroupArtifactVersionOnServer;
import org.softwareFm.jarAndClassPath.server.internal.ProjectForServer;
import org.softwareFm.jarAndClassPath.server.internal.UsageProcessor;
import org.softwareFm.jarAndClassPath.server.internal.UsageReaderForServer;

public interface ISoftwareFmApiFactory {

	public static class Utils {
		public static ICrowdSourcedApi makeClientApiForLocalHost(long timeOutMs, ITransactionManager transactionManager) {
			return ICrowdSourcedApi.Utils.forClient(getLocalConfig(true, timeOutMs), transactionManager);
		}

		public static ICrowdSourcedApi makeClientApiForSoftwareFmServer(long timeOutMs, ITransactionManager transactionManager) {
			return ICrowdSourcedApi.Utils.forClient(getLocalConfig(false, timeOutMs), transactionManager);
		}

		public static LocalConfig getLocalConfig(boolean local, long timeOutMs) {
			File home = new File(System.getProperty("user.home"));
			String host = local ? "localhost" : JarAndPathConstants.softwareFmServerUrl;
			int port = local ? 8080 : 80;
			String remoteGitPrefix = local ? new File(home, ".sfm_remote").getAbsolutePath() : JarAndPathConstants.gitProtocolAndGitServerName;
			File root = new File(home, ".sfm");
			final String urlPrefix = JarAndPathConstants.urlPrefix;
			IExtraReaderWriterConfigurator<LocalConfig> extraReaderWriterConfigurator = getLocalExtraReaderWriterConfigurator();
			LocalConfig localConfig = new LocalConfig(port, 10, timeOutMs, host, root, urlPrefix, remoteGitPrefix, CommonConstants.clientTimeOut, CommonConstants.staleCachePeriod, ICallback.Utils.rethrow(), extraReaderWriterConfigurator);
			return localConfig;
		}

		public static IExtraReaderWriterConfigurator<LocalConfig> getLocalExtraReaderWriterConfigurator() {
			return new IExtraReaderWriterConfigurator<LocalConfig>() {
				@Override
				public void builder(IContainerBuilder builder, LocalConfig localConfig) {
					IProjectTimeGetter projectTimeGetter = IProjectTimeGetter.Utils.timeGetter();
					IRequestGroupReportGeneration requestGroupReportGeneration = IRequestGroupReportGeneration.Utils.httpClient(builder, IResponseCallback.Utils.noCallback());
					builder.register(IProjectTimeGetter.class, projectTimeGetter);
					builder.register(IRequestGroupReportGeneration.class, requestGroupReportGeneration);
					builder.register(IUsageReader.class, IUsageReader.Utils.localUsageReader(builder, localConfig.userUrlGenerator));
				}
			};
		}

		public static ICrowdSourcedApi makeServerApi(int port, long timeOutMs, ITransactionManager transactionManager) {
			return ICrowdSourcedApi.Utils.forServer(getServerConfig(port, timeOutMs), transactionManager);
		}

		public static ServerConfig getServerConfig(int port, long timeOutMs) {
			BasicDataSource dataSource = AbstractLoginDataAccessor.defaultDataSource();
			File root = ICrowdSourcedServer.Utils.makeSfmRoot();
			IIdAndSaltGenerator idAndSaltGenerator = IIdAndSaltGenerator.Utils.uuidGenerators();
			IUserCryptoAccess userCryptoAccess = IUserCryptoAccess.Utils.database(dataSource, idAndSaltGenerator);
			ITakeOnEnrichmentProvider takeOnEnrichment = Utils.getTakeOnEnrichment();
			IExtraCallProcessorFactory extraCallProcessors = IExtraCallProcessorFactory.Utils.noExtraCalls();
			IUsage usage = IUsage.Utils.defaultUsage();
			ICryptoGenerators cryptoGenerators = ICryptoGenerators.Utils.cryptoGenerators();
			Map<String, Callable<Object>> defaultUserValues = Utils.getDefaultUserValues();
			Map<String, Callable<Object>> defaultGroupValues = Utils.getDefaultGroupValues();
			ICallback<Throwable> errorHandler = ICallback.Utils.rethrow();
			IMailer mailer = IMailer.Utils.email("localhost", null, null);
			Callable<Long> timeGetter = Callables.time();
			final String urlPrefix = JarAndPathConstants.urlPrefix;
			IExtraReaderWriterConfigurator<ServerConfig> extraReadWriterConfigurator = Utils.getServerExtraReaderWriterConfigurator(urlPrefix, timeOutMs);
			ServerConfig serverConfig = new ServerConfig(port, 1000, timeOutMs, root, dataSource, takeOnEnrichment, extraCallProcessors, usage, idAndSaltGenerator, cryptoGenerators, userCryptoAccess, urlPrefix, defaultUserValues, defaultGroupValues, errorHandler, mailer, timeGetter, extraReadWriterConfigurator);//
			return serverConfig;
		}

		public static IExtraReaderWriterConfigurator<ServerConfig> getServerExtraReaderWriterConfigurator(final String prefix, final long timeOutMs) {
			return new IExtraReaderWriterConfigurator<ServerConfig>() {
				@Override
				public void builder(IContainerBuilder builder, ServerConfig serverConfig) {
					IUsageReader usageReader = new UsageReaderForServer(builder, serverConfig.userUrlGenerator);
					builder.register(IUsageReader.class, usageReader);

					IGenerateUsageReportGenerator generator = new GenerateUsageProjectGenerator(builder, timeOutMs);
					builder.register(IGenerateUsageReportGenerator.class, generator);

					ProjectForServer project = new ProjectForServer((IUserAndGroupsContainer) builder, serverConfig.userCryptoAccess, serverConfig.userUrlGenerator);
					builder.register(IProject.class, project);

					IJarToGroupArtifactAndVersion jarToGroupArtifactAndVersion = new JarToGroupArtifactVersionOnServer(builder, JarAndPathConstants.jarUrlGenerator(prefix));
					builder.register(IJarToGroupArtifactAndVersion.class, jarToGroupArtifactAndVersion);

					IProjectTimeGetter projectTimeGetter = IProjectTimeGetter.Utils.timeGetter();
					builder.register(IProjectTimeGetter.class, projectTimeGetter);
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
				public ICallProcessor[] makeExtraCalls(IContainer api, ServerConfig serverConfig) {
					return new ICallProcessor[] { //
					new UsageProcessor(api),//
							new GenerateGroupUsageProcessor(api) };
				}
			};
		}
	}

}
