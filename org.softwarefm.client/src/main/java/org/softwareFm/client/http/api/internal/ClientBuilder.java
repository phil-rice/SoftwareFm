/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.client.http.api.internal;

import java.util.Collections;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.softwareFm.client.http.api.IClientBuilder;
import org.softwareFm.client.http.api.IHttpClient;
import org.softwareFm.client.http.constants.HttpClientConstants;
import org.softwareFm.client.http.requests.IRequestBuilder;
import org.softwareFm.client.http.requests.internal.DeleteRequest;
import org.softwareFm.client.http.requests.internal.GetRequest;
import org.softwareFm.client.http.requests.internal.HeadRequest;
import org.softwareFm.client.http.requests.internal.PostRequest;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.services.IServiceExecutor;

public class ClientBuilder implements IClientBuilder {
	public final HttpHost host;
	public final DefaultHttpClient client;
	private final IServiceExecutor executor;
	public List<NameValuePair> defaultHeaders;

	public ClientBuilder() {
		this(HttpClientConstants.defaultHost, HttpClientConstants.defaultPort);
	}

	public ClientBuilder(String host, int port, int threadCount) {
		this(IServiceExecutor.Utils.executor(threadCount), new HttpHost(host, port), makeClient(), null, Collections.<NameValuePair> emptyList());
	}

	public ClientBuilder(String host, int port) {
		this(IServiceExecutor.Utils.defaultExecutor(), new HttpHost(host, port), makeClient(), null, Collections.<NameValuePair> emptyList());
	}

	public ClientBuilder(IServiceExecutor executor, HttpHost httpHost, DefaultHttpClient httpClient, String url, List<NameValuePair> parameters) {
		super();
		this.executor = executor;
		this.host = httpHost;
		this.client = httpClient;
	}

	@Override
	public IClientBuilder withCredentials(String userName, String password) {
		client.getCredentialsProvider().setCredentials(makeAuthScope(), new UsernamePasswordCredentials(userName, password));
		return this;
	}

	@Override
	public IRequestBuilder post(String url) {
		return new PostRequest(executor, host, client, defaultHeaders, url);
	}

	@Override
	public IRequestBuilder get(String url) {
		return new GetRequest(executor, host, client, defaultHeaders, url);
	}

	@Override
	public IRequestBuilder head(String url) {
		return new HeadRequest(executor, host, client, defaultHeaders, url);
	}

	@Override
	public IRequestBuilder delete(String url) {
		return new DeleteRequest(executor, host, client, defaultHeaders, url);
	}

	public AuthScope makeAuthScope() {
		return new AuthScope(host.getHostName(), host.getPort());
	}

	private static DefaultHttpClient makeClient() {
		DefaultHttpClient rawClient = new DefaultHttpClient();
		ClientConnectionManager mgr = rawClient.getConnectionManager();

		DefaultHttpClient actualClient = new DefaultHttpClient(//
				new ThreadSafeClientConnManager(mgr.getSchemeRegistry()));

		actualClient.addRequestInterceptor(new PreemptiveAuthInterceptor(), 0);
		return actualClient;
	}

	@Override
	public void shutdown() {
		executor.shutdown();
	}

	@Override
	public IHttpClient setDefaultHeaders(List<NameValuePair> headers) {
		this.defaultHeaders = Lists.immutableCopy(headers);
		return this;

	}
}