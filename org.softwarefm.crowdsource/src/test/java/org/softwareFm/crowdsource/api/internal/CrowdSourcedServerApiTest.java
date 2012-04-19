/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.internal;

import java.io.File;

import junit.framework.TestCase;

import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.ICrowdSourcedApi;
import org.softwareFm.crowdsource.api.ServerConfig;
import org.softwareFm.crowdsource.api.server.IMailer;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.transaction.ITransactionManager;

public class CrowdSourcedServerApiTest extends TestCase {
	private final File root = new File("root");
	private final ServerConfig serverConfig = ServerConfig.serverConfigForTests(root, IMailer.Utils.noMailer());

	public void testConstructor() {
		ICrowdSourcedApi api1 = ICrowdSourcedApi.Utils.forServer(serverConfig, ITransactionManager.Utils.standard(2, CommonConstants.testTimeOutMs));
		assertTrue(api1 instanceof CrowdSourcedServerApi);
		ICrowdSourcedApi api2 = ICrowdSourcedApi.Utils.forServer(serverConfig, ITransactionManager.Utils.standard(2, CommonConstants.testTimeOutMs));
		assertNotSame(api1, api2);
	}

	public void testGettersReturnsSameObjectEachTime() {
		ICrowdSourcedApi api = ICrowdSourcedApi.Utils.forServer(serverConfig, ITransactionManager.Utils.standard(2, CommonConstants.testTimeOutMs));
		assertTrue(api instanceof CrowdSourcedServerApi);
		IContainer container1 = api.makeContainer();
		IContainer container2 = api.makeContainer();

		assertSame(container1, container2);
	}

}