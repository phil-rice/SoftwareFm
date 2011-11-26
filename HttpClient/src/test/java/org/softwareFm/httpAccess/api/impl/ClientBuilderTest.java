/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.httpAccess.api.impl;

import junit.framework.TestCase;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;
import org.softwareFm.httpClient.api.IClientBuilder;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.api.impl.ClientBuilder;
import org.softwareFm.httpClient.api.impl.PreemptiveAuthInterceptor;
import org.softwareFm.httpClient.constants.HttpClientConstants;

public class ClientBuilderTest extends TestCase {
	@Test
	public void testUtilsMethodsMakesWithHostAndPort() {
		checkHostAndPort(IHttpClient.Utils.builder(), HttpClientConstants.defaultHost, HttpClientConstants.defaultPort);
		checkHostAndPort(IHttpClient.Utils.builder("host", 111), "host", 111);
		checkUserNameAndPassword(IHttpClient.Utils.builder(), null, null);
		checkUserNameAndPassword(IHttpClient.Utils.builder("host", 111), null, null);
	}

	@Test
	public void testWithCredentials() {
		checkUserNameAndPassword(IHttpClient.Utils.builder().withCredentials("user", "pass"), "user", "pass");
	}

	private void checkUserNameAndPassword(IClientBuilder builder, String userName, String password) {
		boolean expectingNull = userName == null && password == null;
		ClientBuilder clientBuilder = (ClientBuilder) builder;
		AuthScope authscope = clientBuilder.makeAuthScope();
		Credentials credentials = clientBuilder.client.getCredentialsProvider().getCredentials(authscope);
		if (expectingNull)
			assertNull(credentials);
		else {
			assertEquals(userName, credentials.getUserPrincipal().getName());
			assertEquals(password, credentials.getPassword());
		}
	}

	private void checkHostAndPort(IClientBuilder builder, String hostname, int port) {
		HttpHost host = ((ClientBuilder) builder).host;
		assertEquals(hostname, host.getHostName());
		assertEquals(port, host.getPort());
		DefaultHttpClient client = ((ClientBuilder) builder).client;
		assertNotNull(client);
		assertTrue(client.getRequestInterceptor(0) instanceof PreemptiveAuthInterceptor);
	}

}