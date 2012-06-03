/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.utilities.http.internal;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.softwarefm.utilities.collections.Lists;
import org.softwarefm.utilities.constants.UtilityMessages;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.http.HttpClientValidationException;
import org.softwarefm.utilities.http.HttpMethod;
import org.softwarefm.utilities.http.IHttpClient;
import org.softwarefm.utilities.http.IResponse;

public class HttpClientBuilder implements IHttpClient {

	public final HttpHost host;
	public final String url;
	public final HttpMethod method;
	public final List<NameValuePair> parameters;
	private final HttpClient client;

	public HttpClientBuilder(HttpClient client, HttpHost host, String url, HttpMethod method, List<NameValuePair> parameters) {
		super();
		this.client = client;
		this.host = host;
		this.url = url;
		this.method = method;
		this.parameters = parameters;
	}

	
	public IResponse execute() {
		try {
			validate();
			String protocolHostAndUrl = protocolHostAndUrl();
			HttpRequestBase base = getRequestBase(protocolHostAndUrl);
			for (NameValuePair pair : parameters)
				base.addHeader(pair.getName(), pair.getValue());

			HttpResponse httpResponse = client.execute(base);
			HttpEntity entity = httpResponse.getEntity();
			String mimeType = findMimeType(entity);

			Response response = new Response(//
					httpResponse.getStatusLine().getStatusCode(), //
					url,//
					entity == null ? "" : EntityUtils.toString(entity),//
					mimeType);
			return response;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	private HttpRequestBase getRequestBase(String protocolHostAndUrl) {
		switch (method) {
		case Post:
			return new HttpPost(protocolHostAndUrl);
		case Get:
			return new HttpGet(protocolHostAndUrl);
		case Head:
			return new HttpHead(protocolHostAndUrl);
		case Delete:
			return new HttpDelete(protocolHostAndUrl);
		default:
			throw new IllegalStateException(method.toString());
		}
	}

	protected String protocolHostAndUrl() {
		String urlWithSlash = url.startsWith("/") ?url: "/" + url;
		return "http://" + host.getHostName() + ":" + host.getPort() + urlWithSlash;
	}

	private String findMimeType(HttpEntity entity) {
		if (entity != null) {
			Header contentType = entity.getContentType();
			if (contentType != null)
				return contentType.getValue();
		}
		return "unknown";
	}

	
	public void validate() {
		List<String> errors = Lists.newList();
		checkNotNull(errors, client, "Client");
		checkNotNull(errors, parameters, "Parameters");
		checkNotNull(errors, host, "Host");
		checkNotNull(errors, method, "Method");
		checkNotNull(errors, url, "Url");
		if (errors.size() > 0)
			throw new HttpClientValidationException(errors);
	}

	private void checkNotNull(List<String> errors, Object object, String name) {
		if (object == null)
			errors.add(MessageFormat.format(UtilityMessages.httpValidationNull, name));
	}

	
	public IHttpClient host(String host) {
		return host(host, 80);
	}

	
	public IHttpClient host(String host, int port) {
		return new HttpClientBuilder(client, new HttpHost(host, port), url, method, parameters);
	}

	
	public IHttpClient post(String url) {
		return new HttpClientBuilder(client, host, url, HttpMethod.Post, parameters);
	}

	
	public IHttpClient get(String url) {
		return new HttpClientBuilder(client, host, url, HttpMethod.Get, parameters);
	}

	
	public IHttpClient head(String url) {
		return new HttpClientBuilder(client, host, url, HttpMethod.Head, parameters);
	}

	
	public IHttpClient delete(String url) {
		return new HttpClientBuilder(client, host, url, HttpMethod.Delete, parameters);
	}

	
	public IHttpClient withParameters(List<NameValuePair> nameAndValues) {
		return new HttpClientBuilder(client, host, url, method, nameAndValues);
	}

	
	public IHttpClient withParams(String... nameAndValue) {
		assert nameAndValue.length % 2 == 0 : nameAndValue.length + "/" + Arrays.asList(nameAndValue);
		List<NameValuePair> nameAndValues = Lists.newList();
		for (int i = 0; i < nameAndValue.length; i += 2)
			nameAndValues.add(new BasicNameValuePair(nameAndValue[i + 0], nameAndValue[i + 1]));
		return new HttpClientBuilder(client, host, url, method, nameAndValues);
	}

	
	public IHttpClient addParam(String name, String value) {
		return new HttpClientBuilder(client, host, url, method, Lists.append(parameters, new BasicNameValuePair(name, value)));
	}
}