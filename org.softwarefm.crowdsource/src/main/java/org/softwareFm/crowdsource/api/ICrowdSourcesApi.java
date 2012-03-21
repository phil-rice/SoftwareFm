package org.softwareFm.crowdsource.api;

import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.api.internal.AbstractCrowdSourceReadWriterApi;
import org.softwareFm.crowdsource.api.internal.AbstractCrowdSourcesApi;
import org.softwareFm.crowdsource.api.internal.CrowdSourcedLocalApi;
import org.softwareFm.crowdsource.api.internal.CrowdSourcedServerApi;
import org.softwareFm.crowdsource.api.internal.ServerDoers;
import org.softwareFm.crowdsource.api.server.IServerDoers;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;

public interface ICrowdSourcesApi {

	/** this instantiates a server that listeners to the given port. */
	ICrowdSourcedServer getServer();

	ICrowdSourceReaderApi makeReader();

	ICrowdSourceReadWriteApi makeReadWriter();

	void shutdown();

	public static class Utils {
		public static ICrowdSourcesApi forServer(final ServerConfig serverConfig) {
			return new CrowdSourcedServerApi(serverConfig, new IFunction1<ICrowdSourceReadWriteApi, IServerDoers>() {
				@Override
				public IServerDoers apply(ICrowdSourceReadWriteApi from) throws Exception {
					return new ServerDoers(serverConfig, from);
				}
			});
		}

		public static ICrowdSourcesApi forServer(ServerConfig serverConfig, IFunction1<ICrowdSourceReadWriteApi, IServerDoers> serverDoersCreator) {
			return new CrowdSourcedServerApi(serverConfig, serverDoersCreator);
		}

		public static ICrowdSourcesApi forClient(LocalConfig localConfig) {
			return new CrowdSourcedLocalApi(localConfig);
		}

		public static ICrowdSourcesApi forTests(IExtraReaderWriterConfigurator<ApiConfig> configurator, ApiConfig apiConfig) {
			final AbstractCrowdSourceReadWriterApi readWriter = new AbstractCrowdSourceReadWriterApi() {
				@Override
				public IGitOperations gitOperations() {
					throw new UnsupportedOperationException();
				}
			};
			configurator.builder(readWriter, apiConfig);
			return new AbstractCrowdSourcesApi() {

				@Override
				public ICrowdSourceReaderApi makeReader() {
					return readWriter;
				}

				@Override
				public ICrowdSourceReadWriteApi makeReadWriter() {
					return readWriter;
				}
			};
		}

	}
}
