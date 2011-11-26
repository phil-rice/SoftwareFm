/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.httpClient.requests.impl;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.softwareFm.httpClient.requests.IRequestBuilder;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.httpClient.response.impl.Response;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.services.IServiceExecutor;

public abstract class AbstractRequestBuilder implements IRequestBuilder {

	public final HttpHost host;
	public final HttpClient client;
	public final String url;
	public final List<NameValuePair> parameters = Lists.newList();
	public final IServiceExecutor executor;
	public final List<NameValuePair> defaultHeaders;

	public AbstractRequestBuilder(IServiceExecutor executor, HttpHost host, HttpClient client, List<NameValuePair> defaultHeaders, String url) {
		this.executor = executor;
		this.host = host;
		this.client = client;
		this.defaultHeaders = Lists.immutableCopy(defaultHeaders);
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
				for (NameValuePair pair : Lists.nullSafe(defaultHeaders))
					base.addHeader(pair.getName(), pair.getValue());

				HttpResponse httpResponse = client.execute(base);
				HttpEntity entity = httpResponse.getEntity();
				Response response = new Response(url,//
						httpResponse.getStatusLine().getStatusCode(), //
						entity == null ? "" : EntityUtils.toString(entity));
				callback.process(response);
				return null;
			}
		});
	}

	abstract protected HttpRequestBase getRequestBase(String protocolHostAndUrl) throws Exception;
}