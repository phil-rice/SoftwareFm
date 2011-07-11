package org.arc4eclipse.httpClient.api;

import org.arc4eclipse.httpClient.api.impl.ClientBuilder;
import org.arc4eclipse.httpClient.constants.HttpClientConstants;
import org.arc4eclipse.httpClient.requests.IRequestBuilder;

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

}
