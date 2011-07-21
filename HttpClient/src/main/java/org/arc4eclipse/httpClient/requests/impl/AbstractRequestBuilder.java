package org.arc4eclipse.httpClient.requests.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.arc4eclipse.httpClient.api.IServiceExecutor;
import org.arc4eclipse.httpClient.requests.IRequestBuilder;

public abstract class AbstractRequestBuilder implements IRequestBuilder {

	public final HttpHost host;
	public final HttpClient client;
	public final String url;
	public final List<NameValuePair> parameters = new ArrayList<NameValuePair>();
	public final IServiceExecutor executor;

	public AbstractRequestBuilder(IServiceExecutor executor, HttpHost host, HttpClient client, String url) {
		this.executor = executor;
		this.host = host;
		this.client = client;
		this.url = url;
	}

	@Override
	public IRequestBuilder addParam(String name, String value) {
		parameters.add(new BasicNameValuePair(name, value));
		return this;
	}

	@Override
	public IRequestBuilder addParams(String... nameAndValue) {
		for (int i = 0; i < nameAndValue.length; i += 2)
			parameters.add(new BasicNameValuePair(nameAndValue[i + 0], nameAndValue[i + 1]));
		return this;
	}

	@Override
	public IRequestBuilder addParams(List<NameValuePair> nameAndValues) {
		parameters.addAll(nameAndValues);
		return this;
	}

	protected String protocolHostAndUrl() {
		return "http://" + host.getHostName() + ":" + host.getPort() + url;
	}

	@Override
	public String getUrl() {
		return url;
	}
}
