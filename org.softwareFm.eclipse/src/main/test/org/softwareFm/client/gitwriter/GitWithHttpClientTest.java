package org.softwareFm.client.gitwriter;

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
