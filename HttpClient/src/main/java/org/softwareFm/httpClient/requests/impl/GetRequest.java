package org.softwareFm.httpClient.requests.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.softwareFm.httpClient.api.IServiceExecutor;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.httpClient.response.impl.Response;

public class GetRequest extends AbstractRequestBuilder {

	public GetRequest(IServiceExecutor executor, HttpHost host, HttpClient client, String url) {
		super(executor, host, client, url);
	}

	@Override
	public Future<?> execute(final IResponseCallback callback) {
		return executor.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				String protocolHostAndUrl = protocolHostAndUrl();
				HttpGet get = new HttpGet(protocolHostAndUrl);
				HttpResponse httpResponse = client.execute(get);
				Response response = new Response(url, httpResponse.getStatusLine().getStatusCode(), EntityUtils.toString(httpResponse.getEntity()));
				callback.process(response);
				return null;
			}
		});
	}
}