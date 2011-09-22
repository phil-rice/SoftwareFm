package org.softwareFm.httpClient.requests.impl;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpRequestBase;
import org.softwareFm.httpClient.api.IServiceExecutor;

public class DeleteRequest extends AbstractRequestBuilder {

	public DeleteRequest(IServiceExecutor executor, HttpHost host, HttpClient client, String url) {
		super(executor, host, client, url);
	}


	@Override
	protected HttpRequestBase getRequestBase(String protocolHostAndUrl) {
		HttpDelete delete = new HttpDelete(protocolHostAndUrl);
		return delete;
	}

}
