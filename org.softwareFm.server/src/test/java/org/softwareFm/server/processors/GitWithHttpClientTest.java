package org.softwareFm.server.processors;

import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.server.GitTest;
import org.softwareFm.server.constants.CommonConstants;

public class GitWithHttpClientTest extends GitTest {

	private IHttpClient httpClient;
	
	public IHttpClient getHttpClient() {
		if (httpClient == null)
			httpClient = IHttpClient.Utils.builder("localhost", CommonConstants.);
		return httpClient;
	}

	@Override
	protected void tearDown() throws Exception {
		if (httpClient != null)
			httpClient.shutdown();
	}

}
