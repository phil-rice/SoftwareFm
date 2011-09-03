package org.softwareFm.httpClient.requests.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.util.EntityUtils;
import org.softwareFm.httpClient.api.IServiceExecutor;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.httpClient.response.impl.Response;

public class DeleteRequest extends AbstractRequestBuilder {

	public DeleteRequest(IServiceExecutor executor, HttpHost host, HttpClient client, String url) {
		super(executor, host, client, url);
	}

	@Override
	public Future<?> execute(final IResponseCallback callback) {
		return executor.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				String param = protocolHostAndUrl();
				HttpDelete delete = new HttpDelete(param);
				HttpResponse httpResponse = client.execute(delete);
				HttpEntity entity = httpResponse.getEntity();
				Response response = new Response(url, httpResponse.getStatusLine().getStatusCode(), entity == null ? null : EntityUtils.toString(entity));
				callback.process(response);
				return null;
			}
		});
	}

}
