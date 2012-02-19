/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card;

import org.softwareFm.client.http.api.IHttpClient;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.server.GitTest;

abstract public class GitWithHttpClientTest extends GitTest {

	private IHttpClient httpClient;

	public IHttpClient getHttpClient() {
		if (httpClient == null)
			httpClient = IHttpClient.Utils.builder("localhost", CommonConstants.testPort);
		return httpClient;
	}

	@Override
	protected void tearDown() throws Exception {
		if (httpClient != null)
			httpClient.shutdown();
	}

}