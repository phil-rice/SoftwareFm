package org.softwareFm.httpClient.requests.impl;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.softwareFm.httpClient.api.IServiceExecutor;
import org.softwareFm.httpClient.requests.IRequestBuilder;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.httpClient.response.impl.Response;
import org.softwareFm.utilities.collections.Lists;

public abstract class AbstractRequestBuilder implements IRequestBuilder {

	public final HttpHost host;
	public final HttpClient client;
	public final String url;
	public final List<NameValuePair> headers = Lists.newList();
	public final List<NameValuePair> parameters = Lists.newList();
	public final IServiceExecutor executor;

	public AbstractRequestBuilder(IServiceExecutor executor, HttpHost host, HttpClient client, String url) {
		this.executor = executor;
		this.host = host;
		this.client = client;
		this.url = url.startsWith("/") ? url : "/" + url;
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

	@Override
	public IRequestBuilder addHeader(String name, String value) {
		headers.add(new BasicNameValuePair(name, value));
		return this;
	}

	protected String protocolHostAndUrl() {
		return "http://" + host.getHostName() + ":" + host.getPort() + url;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public Future<?> execute(final IResponseCallback callback) {
		return executor.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				String protocolHostAndUrl = protocolHostAndUrl();
				HttpRequestBase base = getRequestBase(protocolHostAndUrl);
				for(NameValuePair pair: headers)
					base.addHeader(pair.getName(), pair.getValue());
				
				HttpResponse httpResponse = client.execute(base);
				Response response = new Response(url,//
						httpResponse.getStatusLine().getStatusCode(), //
						EntityUtils.toString(httpResponse.getEntity()));
				callback.process(response);
				return null;
			}
		});
	}

	abstract protected HttpRequestBase getRequestBase(String protocolHostAndUrl) throws Exception;
}
