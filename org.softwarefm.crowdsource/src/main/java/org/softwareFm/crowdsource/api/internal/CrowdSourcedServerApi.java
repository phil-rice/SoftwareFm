package org.softwareFm.crowdsource.api.internal;

import org.softwareFm.crowdsource.api.ICrowdSourceReadWriteApi;
import org.softwareFm.crowdsource.api.ICrowdSourceReaderApi;
import org.softwareFm.crowdsource.api.ICrowdSourcedServer;
import org.softwareFm.crowdsource.api.ServerConfig;
import org.softwareFm.crowdsource.api.server.ICallProcessor;
import org.softwareFm.crowdsource.api.server.IServerDoers;
import org.softwareFm.crowdsource.server.internal.CrowdSourcedServer;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;

public class CrowdSourcedServerApi extends AbstractCrowdSourcesApi {

	private final ServerConfig serverConfig;
	private final CrowdSourcedServerReadWriterApi crowdSourcedServerReadWriterApi;
	private final IServerDoers serverDoers;
	private ICrowdSourcedServer server;
	private final Object lock = new Object();

	public CrowdSourcedServerApi(ServerConfig serverConfig, IFunction1<ICrowdSourceReadWriteApi, IServerDoers> serverDoersCreator) {
		this.serverConfig = serverConfig;
		crowdSourcedServerReadWriterApi = new CrowdSourcedServerReadWriterApi(serverConfig);
		serverDoers = Functions.call(serverDoersCreator, crowdSourcedServerReadWriterApi);
		serverConfig.extraReaderWriterConfigurator.builder(crowdSourcedServerReadWriterApi, serverConfig);
	}

	@Override
	public ICrowdSourceReaderApi makeReader() {
		return  crowdSourcedServerReadWriterApi;
	}

	@Override
	public ICrowdSourceReadWriteApi makeReadWriter() {
		return crowdSourcedServerReadWriterApi;
	}

	@Override
	public ICrowdSourcedServer getServer() {
		if (server == null) {
			synchronized (lock) {
				if (server == null) {
					ICallProcessor callProcessor = ICallProcessor.Utils.softwareFmProcessCall(crowdSourcedServerReadWriterApi, serverDoers, serverConfig);
					server = new CrowdSourcedServer(serverConfig.port, serverConfig.workerThreads, callProcessor, ICallback.Utils.sysErrCallback(), serverConfig.usage);
				}
			}
		}
		return server;
	}

	public IServerDoers getServerDoers() {
		return serverDoers;
	}

	@Override
	public void shutdown() {
		if (server != null)
			server.shutdown();

	}
}
