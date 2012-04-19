/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api;

import java.io.File;

import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.api.internal.AbstractCrowdSourcesApi;
import org.softwareFm.crowdsource.api.internal.Container;
import org.softwareFm.crowdsource.api.internal.CrowdSourcedLocalApi;
import org.softwareFm.crowdsource.api.internal.CrowdSourcedServerApi;
import org.softwareFm.crowdsource.api.internal.ServerDoers;
import org.softwareFm.crowdsource.api.server.IServerDoers;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.softwareFm.crowdsource.utilities.transaction.ITransactionManager;

public interface ICrowdSourcedApi {

	/** this instantiates a server that listeners to the given port. */
	ICrowdSourcedServer getServer();

	IContainer makeContainer();

	IUserAndGroupsContainer makeUserAndGroupsContainer();

	void shutdown();

	public static class Utils {
		public static ICrowdSourcedApi forServer(final ServerConfig serverConfig, ITransactionManager transactionManager) {
			return new CrowdSourcedServerApi(serverConfig, transactionManager, new IFunction1<IUserAndGroupsContainer, IServerDoers>() {
				@Override
				public IServerDoers apply(IUserAndGroupsContainer from) throws Exception {
					return new ServerDoers(serverConfig, from);
				}
			});
		}

		public static ICrowdSourcedApi forServer(ServerConfig serverConfig, ITransactionManager transactionManager, IFunction1<IUserAndGroupsContainer, IServerDoers> serverDoersCreator) {
			return new CrowdSourcedServerApi(serverConfig, transactionManager, serverDoersCreator);
		}

		public static ICrowdSourcedApi forClient(LocalConfig localConfig, ITransactionManager transactionManager) {
			return new CrowdSourcedLocalApi(localConfig, transactionManager);
		}

		// TODO This should deal with swt threading in the transaction manager
		public static ICrowdSourcedApi forTests(IExtraReaderWriterConfigurator<ApiConfig> configurator, ITransactionManager transactionManager, final File root) {
			final IGitOperations gitOperations = IGitOperations.Utils.gitOperations(root);
			final Container readWriter = new Container(transactionManager, gitOperations) {
			};
			configurator.builder(readWriter, new ApiConfig(1, 1, CommonConstants.testTimeOutMs, CommonConstants.staleCachePeriodForTest, root, "junk", null, IExtraReaderWriterConfigurator.Utils.noExtras(), Callables.<Long> exceptionIfCalled()));
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