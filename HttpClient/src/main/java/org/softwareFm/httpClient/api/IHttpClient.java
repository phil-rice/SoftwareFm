/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.httpClient.api;

import java.util.List;

import org.apache.http.NameValuePair;
import org.softwareFm.httpClient.api.impl.ClientBuilder;
import org.softwareFm.httpClient.constants.HttpClientConstants;
import org.softwareFm.httpClient.requests.IRequestBuilder;

public interface IHttpClient {
	public static class Utils {
		public static IClientBuilder builder() {
			return new ClientBuilder();
		}

		public static IClientBuilder builderWithThreads(String host, int port, int threadCount) {
			return new ClientBuilder(host, port, threadCount);
		}
		public static IClientBuilder builder(String host, int port) {
			return new ClientBuilder(host, port);
		}

		public static IHttpClient defaultClient() {
			return builder().withCredentials(HttpClientConstants.userName, HttpClientConstants.password);
		}
	}

	IHttpClient setDefaultHeaders(List<NameValuePair> headers);

	IRequestBuilder post(String url);

	IRequestBuilder get(String url);

	IRequestBuilder head(String url);

	IRequestBuilder delete(String url);

	void shutdown();

}