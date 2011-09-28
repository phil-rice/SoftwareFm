package org.softwareFm.httpClient.api.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.util.EntityUtils;
import org.softwareFm.httpClient.api.IHttpGetter;
import org.softwareFm.httpClient.api.IHttpGetterCallback;
import org.softwareFm.httpClient.response.impl.Response;

public class HttpGetter implements IHttpGetter {

	private final ExecutorService service;
	private final DefaultHttpClient client;

	public HttpGetter(ExecutorService service) {
		this.service = service;
		DefaultHttpClient rawClient = new DefaultHttpClient();
		ClientConnectionManager mgr = rawClient.getConnectionManager();
		client = new DefaultHttpClient(new ThreadSafeClientConnManager(mgr.getSchemeRegistry()));
	}

	@Override
	public Future<Response> getFromUrl(String type, final String url, final IHttpGetterCallback callback) {
		return service.submit(new Callable<Response>() {
			@Override
			public Response call() throws Exception {
				HttpGet get = new HttpGet(url.trim());
				HttpResponse httpResponse = client.execute(get);
				HttpEntity entity = httpResponse.getEntity();
				String reply = entity == null?"":EntityUtils.toString(entity);
				
				Response response = new Response(url, httpResponse.getStatusLine().getStatusCode(), reply);
				callback.processGet(url, response);
				return response;
			}
		});
	}

}
