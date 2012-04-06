/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.api;

import org.apache.log4j.xml.DOMConfigurator;
import org.softwareFm.crowdsource.api.ICrowdSourcedApi;
import org.softwareFm.crowdsource.api.ICrowdSourcedServer;
import org.softwareFm.crowdsource.api.ServerConfig;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.transaction.ITransactionManager;
import org.softwareFm.jarAndClassPath.api.ISoftwareFmApiFactory.Utils;

public class SoftwareFmServer {

	public static void main(String[] args) {
		DOMConfigurator.configure(System.getProperty("user.home") + "/log4j.xml");
		int port = ICrowdSourcedServer.Utils.port(args);

		ServerConfig serverConfig = Utils.getServerConfig(port, CommonConstants.clientTimeOut);
		ICrowdSourcedApi api = ICrowdSourcedApi.Utils.forServer(serverConfig, ITransactionManager.Utils.standard(serverConfig.workerThreads));
		api.getServer();
	}

}