package org.softwareFm.crowdsource.api;

import java.io.File;

import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.api.internal.AbstractCrowdSourceReadWriterApi;
import org.softwareFm.crowdsource.api.internal.AbstractCrowdSourcesApi;
import org.softwareFm.crowdsource.api.internal.CrowdSourcedLocalApi;
import org.softwareFm.crowdsource.api.internal.CrowdSourcedServerApi;
import org.softwareFm.crowdsource.api.internal.ServerDoers;
import org.softwareFm.crowdsource.api.server.IServerDoers;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;

public interface ICrowdSourcedApi {

	/** this instantiates a server that listeners to the given port. */
	ICrowdSourcedServer getServer();

	IContainer makeContainer();

	IUserAndGroupsContainer makeUserAndGroupsContainer();

	void shutdown();

	public static class Utils {
		public static ICrowdSourcedApi forServer(final ServerConfig serverConfig) {
			return new CrowdSourcedServerApi(serverConfig, new IFunction1<IUserAndGroupsContainer, IServerDoers>() {
				@Override
				public IServerDoers apply(IUserAndGroupsContainer from) throws Exception {
					return new ServerDoers(serverConfig, from);
				}
			});
		}

		public static ICrowdSourcedApi forServer(ServerConfig serverConfig, IFunction1<IUserAndGroupsContainer, IServerDoers> serverDoersCreator) {
			return new CrowdSourcedServerApi(serverConfig, serverDoersCreator);
		}

		public static ICrowdSourcedApi forClient(LocalConfig localConfig) {
			return new CrowdSourcedLocalApi(localConfig);
		}

		public static ICrowdSourcedApi forTests(IExtraReaderWriterConfigurator<ApiConfig> configurator, final File root) {
			final IGitOperations gitOperations = IGitOperations.Utils.gitOperations(root);
			final AbstractCrowdSourceReadWriterApi readWriter = new AbstractCrowdSourceReadWriterApi() {
				@Override
				public IGitOperations gitOperations() {
					return gitOperations;
				}
			};
			configurator.builder(readWriter, null);
			return new AbstractCrowdSourcesApi() {

				@Override
				public IContainer makeContainer() {
					return readWriter;
				}

				@Override
				public IUserAndGroupsContainer makeUserAndGroupsContainer() {
					return readWriter;
				}
			};
		}

	}

}
