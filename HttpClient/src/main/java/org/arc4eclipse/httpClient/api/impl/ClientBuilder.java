package org.arc4eclipse.httpClient.api.impl;

import java.util.Collections;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.arc4eclipse.httpClient.api.IClientBuilder;
import org.arc4eclipse.httpClient.constants.HttpClientConstants;
import org.arc4eclipse.httpClient.requests.IRequestBuilder;
import org.arc4eclipse.httpClient.requests.impl.GetRequest;
import org.arc4eclipse.httpClient.requests.impl.PostRequest;

public class ClientBuilder implements IClientBuilder {
	public final HttpHost host;
	public final DefaultHttpClient client;

	public ClientBuilder() {
		this(HttpClientConstants.defaultHost, HttpClientConstants.defaultPort);
	}

	@SuppressWarnings("unchecked")
	public ClientBuilder(String host, int port) {
		this(new HttpHost(host, port), makeClient(), null, Collections.EMPTY_LIST);
	}

	public ClientBuilder(HttpHost httpHost, DefaultHttpClient httpClient, String url, List<NameValuePair> parameters) {
		super();
		this.host = httpHost;
		this.client = httpClient;
	}

	
	public IClientBuilder withCredentials(String userName, String password) {
		client.getCredentialsProvider().setCredentials(makeAuthScope(), new UsernamePasswordCredentials(userName, password));
		return this;
	}

	
	public IRequestBuilder post(String url) {
		return new PostRequest(host, client, url);
	}

	
	public IRequestBuilder get(String url) {
		return new GetRequest(host, client, url);
	}

	public AuthScope makeAuthScope() {
		return new AuthScope(host.getHostName(), host.getPort());
	}

	private static DefaultHttpClient makeClient() {
		DefaultHttpClient client = new DefaultHttpClient();
		client.addRequestInterceptor(new PreemptiveAuthInterceptor(), 0);
		return client;
	}
}
