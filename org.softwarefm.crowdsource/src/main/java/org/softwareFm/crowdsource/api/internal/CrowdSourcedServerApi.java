package org.softwareFm.crowdsource.api.internal;

import java.sql.SQLException;

import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.ICrowdSourcedServer;
import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
import org.softwareFm.crowdsource.api.ServerConfig;
import org.softwareFm.crowdsource.api.server.ICallProcessor;
import org.softwareFm.crowdsource.api.server.IServerDoers;
import org.softwareFm.crowdsource.server.internal.CrowdSourcedServer;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;

public class CrowdSourcedServerApi extends AbstractCrowdSourcesApi {

	private final ServerConfig serverConfig;
	private final CrowdSourcedServerReadWriterApi crowdSourcedServerReadWriterApi;
	private final IServerDoers serverDoers;
	private ICrowdSourcedServer server;
	private final Object lock = new Object();

	@SuppressWarnings("unchecked")
	public CrowdSourcedServerApi(ServerConfig serverConfig, IFunction1<IUserAndGroupsContainer, IServerDoers> serverDoersCreator) {
		this.serverConfig = serverConfig;
		crowdSourcedServerReadWriterApi = new CrowdSourcedServerReadWriterApi(serverConfig);
		serverDoers = Functions.call(serverDoersCreator, crowdSourcedServerReadWriterApi);
		serverConfig.extraReaderWriterConfigurator.builder(crowdSourcedServerReadWriterApi, serverConfig);
	}

	@Override
	public IContainer makeContainer() {
		return crowdSourcedServerReadWriterApi;
	}
	@Override
	public IUserAndGroupsContainer makeUserAndGroupsContainer() {
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
		try {
			serverConfig.dataSource.close();
		} catch (SQLException e) {
			throw WrappedException.wrap(e);
		}

	}

}