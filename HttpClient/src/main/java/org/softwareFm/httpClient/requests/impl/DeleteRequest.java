package org.softwareFm.httpClient.requests.impl;

import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpRequestBase;
import org.softwareFm.utilities.services.IServiceExecutor;

public class DeleteRequest extends AbstractRequestBuilder {

	public DeleteRequest(IServiceExecutor executor, HttpHost host, HttpClient client, List<NameValuePair> defaultHeaders, String url) {
		super(executor, host, client, defaultHeaders, url);
	}

	@Override
	protected HttpRequestBase getRequestBase(String protocolHostAndUrl) {
		HttpDelete delete = new HttpDelete(protocolHostAndUrl);
		return delete;
	}

}
