/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.internal;

import java.io.IOException;
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
import org.softwareFm.crowdsource.utilities.transaction.ITransactionManager;

public class CrowdSourcedServerApi extends AbstractCrowdSourcesApi {

	private final ServerConfig serverConfig;
	private final ContainerForServer containerForServer;
	private final IServerDoers serverDoers;
	private ICrowdSourcedServer server;
	private final Object lock = new Object();

	@SuppressWarnings("unchecked")
	public CrowdSourcedServerApi(ServerConfig serverConfig, ITransactionManager transactionManager, IFunction1<IUserAndGroupsContainer, IServerDoers> serverDoersCreator) {
		this.serverConfig = serverConfig;
		containerForServer = new ContainerForServer(serverConfig, transactionManager);
		serverDoers = Functions.call(serverDoersCreator, containerForServer);
		ICallProcessor callProcessor = ICallProcessor.Utils.softwareFmProcessCall(containerForServer, serverDoers, serverConfig);
		containerForServer.register(ICallProcessor.class, callProcessor);
		serverConfig.extraReaderWriterConfigurator.builder(containerForServer, serverConfig);
	}

	@Override
	public IContainer makeContainer() {
		return containerForServer;
	}

	@Override
	public IUserAndGroupsContainer makeUserAndGroupsContainer() {
		return containerForServer;
	}

	@Override
	public ICrowdSourcedServer getServer() {
		if (server == null) {
			synchronized (lock) {
				if (server == null) {
					try {
						server = new CrowdSourcedServer(serverConfig.port, containerForServer, ICallback.Utils.sysErrCallback(), serverConfig.usage);
					} catch (IOException e) {
						throw WrappedException.wrap(e);
					}
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