package org.softwareFm.httpClient.api.impl;

import java.util.Collections;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.softwareFm.httpClient.api.IClientBuilder;
import org.softwareFm.httpClient.api.IServiceExecutor;
import org.softwareFm.httpClient.constants.HttpClientConstants;
import org.softwareFm.httpClient.requests.IRequestBuilder;
import org.softwareFm.httpClient.requests.impl.DeleteRequest;
import org.softwareFm.httpClient.requests.impl.GetRequest;
import org.softwareFm.httpClient.requests.impl.PostRequest;

public class ClientBuilder implements IClientBuilder {
	public final HttpHost host;
	public final DefaultHttpClient client;
	private final IServiceExecutor executor;

	public ClientBuilder() {
		this(HttpClientConstants.defaultHost, HttpClientConstants.defaultPort);
	}

	@SuppressWarnings("unchecked")
	public ClientBuilder(String host, int port) {
		this(IServiceExecutor.Utils.defaultExecutor(), new HttpHost(host, port), makeClient(), null, Collections.EMPTY_LIST);
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
		return new PostRequest(executor, host, client, url);
	}

	@Override
	public IRequestBuilder get(String url) {
		return new GetRequest(executor, host, client, url);
	}

	@Override
	public IRequestBuilder delete(String url) {
		return new DeleteRequest(executor, host, client, url);
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
}
