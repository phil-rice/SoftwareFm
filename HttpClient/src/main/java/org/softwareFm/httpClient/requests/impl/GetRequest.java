package org.softwareFm.httpClient.requests.impl;

import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.softwareFm.httpClient.api.IServiceExecutor;

public class GetRequest extends AbstractRequestBuilder {

	public GetRequest(IServiceExecutor executor, HttpHost host, HttpClient client, List<NameValuePair> defaultHeaders, String url) {
		super(executor, host, client, defaultHeaders, url);
	}

	@Override
	protected HttpRequestBase getRequestBase(String protocolHostAndUrl) {
		HttpGet get = new HttpGet(protocolHostAndUrl);
		return get;
	}
}