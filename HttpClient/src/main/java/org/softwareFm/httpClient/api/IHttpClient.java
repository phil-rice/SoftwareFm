package org.softwareFm.httpClient.api;

import org.softwareFm.httpClient.api.impl.ClientBuilder;
import org.softwareFm.httpClient.constants.HttpClientConstants;
import org.softwareFm.httpClient.requests.IRequestBuilder;

public interface IHttpClient {
	public static class Utils {
		public static IClientBuilder builder() {
			return new ClientBuilder();
		}

		public static IClientBuilder builder(String host, int port) {
			return new ClientBuilder(host, port);
		}

		public static IHttpClient defaultClient() {
			return builder().withCredentials(HttpClientConstants.userName, HttpClientConstants.password);
		}
	}

	IRequestBuilder post(String url);

	IRequestBuilder get(String url);

	IRequestBuilder delete(String url);

	void shutdown();

}
