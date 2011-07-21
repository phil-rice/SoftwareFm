package org.arc4eclipse.httpClient.requests.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.arc4eclipse.httpClient.api.IServiceExecutor;
import org.arc4eclipse.httpClient.requests.IResponseCallback;
import org.arc4eclipse.httpClient.response.impl.Response;

public class PostRequest extends AbstractRequestBuilder {

	public PostRequest(IServiceExecutor executor, HttpHost host, HttpClient client, String url) {
		super(executor, host, client, url);
	}

	@Override
	public Future<?> execute(final IResponseCallback callback) {
		return executor.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				String param = protocolHostAndUrl();
				HttpPost post = new HttpPost(param);
				post.setEntity(new UrlEncodedFormEntity(parameters));
				HttpResponse httpResponse = client.execute(post);
				Response response = new Response(url, httpResponse.getStatusLine().getStatusCode(), EntityUtils.toString(httpResponse.getEntity()));
				callback.process(response);
				return null;
			}
		});
	}

}
