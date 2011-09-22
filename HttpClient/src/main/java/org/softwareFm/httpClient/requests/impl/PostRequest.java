package org.softwareFm.httpClient.requests.impl;

import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.softwareFm.httpClient.api.IServiceExecutor;

public class PostRequest extends AbstractRequestBuilder {

	public PostRequest(IServiceExecutor executor, HttpHost host, HttpClient client, List<NameValuePair> defaultHeaders, String url) {
		super(executor, host, client, defaultHeaders, url);
	}

	@Override
	protected HttpRequestBase getRequestBase(String protocolHostAndUrl) throws Exception {
		HttpPost post = new HttpPost(protocolHostAndUrl);
		post.setEntity(new UrlEncodedFormEntity(parameters));
		return post;
	}
}
