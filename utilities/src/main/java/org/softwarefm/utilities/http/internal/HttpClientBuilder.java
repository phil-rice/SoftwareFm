/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.utilities.http.internal;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.softwarefm.utilities.collections.Lists;
import org.softwarefm.utilities.constants.UtilityMessages;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.http.HttpClientValidationException;
import org.softwarefm.utilities.http.HttpMethod;
import org.softwarefm.utilities.http.IHttpClient;
import org.softwarefm.utilities.http.IResponse;
import org.softwarefm.utilities.strings.Strings;

public class HttpClientBuilder implements IHttpClient {

	public final HttpHost host;
	public final String url;
	public final HttpMethod method;
	public final List<NameValuePair> headers;
	public final List<NameValuePair> parameters;
	private final HttpClient client;
	private final byte[] entity;

	public HttpClientBuilder(HttpClient client, HttpHost host, String url, HttpMethod method, byte[] entity, List<NameValuePair> headers, List<NameValuePair> parameters) {
		super();
		this.client = client;
		this.host = host;
		this.url = url;
		this.method = method;
		this.entity = entity;
		this.headers = headers;
		this.parameters = parameters;
	}

	public IResponse execute() {
		try {
			validate();
			String protocolHostAndUrl = protocolHostAndUrl();
			HttpRequestBase base = getRequestBase(protocolHostAndUrl);

			if (headers != null)
				for (NameValuePair pair : headers)
					base.addHeader(pair.getName(), pair.getValue());
			if (base instanceof HttpPost) {
				HttpPost post = (HttpPost) base;
				if (parameters.size() > 0)
					post.setEntity(getParamsEntity());
				else if (entity != null)
					post.setEntity(new ByteArrayEntity(entity));
			}
			HttpResponse httpResponse = client.execute(base);
			HttpEntity entity = httpResponse.getEntity();
			String mimeType = findMimeType(entity);

			boolean zipped = IHttpClient.Utils.headerEquals(httpResponse, "Content-Encoding", "gzip");
			String responseString = entity == null ? null : (zipped ? Strings.unzip(EntityUtils.toByteArray(entity)) : EntityUtils.toString(entity));
			List<Header> headers = Arrays.asList(httpResponse.getAllHeaders());
			Response response = new Response(//
					httpResponse.getStatusLine().getStatusCode(), //
					url,//
					entity == null ? "" : responseString,//
					mimeType, headers);
			return response;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	private UrlEncodedFormEntity getParamsEntity() {
		try {
			return new UrlEncodedFormEntity(parameters);
		} catch (UnsupportedEncodingException e) {
			throw WrappedException.wrap(e);
		}
	}

	private HttpRequestBase getRequestBase(String protocolHostAndUrl) {
		switch (method) {
		case POST:
			return new HttpPost(protocolHostAndUrl);
		case PUT:
			return new HttpPut(protocolHostAndUrl);
		case GET:
			return new HttpGet(protocolHostAndUrl);
		case HEAD:
			return new HttpHead(protocolHostAndUrl);
		case DELETE:
			return new HttpDelete(protocolHostAndUrl);
		default:
			throw new IllegalStateException(method.toString());
		}
	}

	protected String protocolHostAndUrl() {
		String result = "http://" + Strings.url(host.getHostName() + ":" + host.getPort(), url);
		return result;
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
		if (method != HttpMethod.POST)
			if (parameters.size() > 0 || entity != null)
				errors.add("Cannot have parameters/entity unless post");
		if (entity != null && parameters.size() > 0)
			errors.add("Cannot have entity and parameters");
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
		return new HttpClientBuilder(client, new HttpHost(host, port), url, method, entity, headers, parameters);
	}

	public IHttpClient put(String urlPattern, Object... params) {
		return new HttpClientBuilder(client, host, MessageFormat.format(urlPattern, params), HttpMethod.PUT, entity, headers, parameters);
	}

	public IHttpClient post(String urlPattern, Object... params) {
		return new HttpClientBuilder(client, host, MessageFormat.format(urlPattern, params), HttpMethod.POST, entity, headers, parameters);
	}

	public IHttpClient get(String urlPattern, Object... params) {
		return new HttpClientBuilder(client, host, MessageFormat.format(urlPattern, params), HttpMethod.GET, entity, headers, parameters);
	}

	public IHttpClient head(String urlPattern, Object... params) {
		return new HttpClientBuilder(client, host, MessageFormat.format(urlPattern, params), HttpMethod.HEAD, entity, headers, parameters);
	}

	public IHttpClient delete(String urlPattern, Object... params) {
		return new HttpClientBuilder(client, host, MessageFormat.format(urlPattern, params), HttpMethod.DELETE, entity, headers, parameters);
	}

	@Override
	public IHttpClient method(HttpMethod method, String urlPattern, Object... params) {
		return new HttpClientBuilder(client, host, MessageFormat.format(urlPattern, params), method, entity, headers, parameters);
	}

	public IHttpClient withParameters(List<NameValuePair> nameAndValues) {
		return new HttpClientBuilder(client, host, url, method, entity, headers, nameAndValues);
	}

	public IHttpClient withEntity(String entity) {
		return new HttpClientBuilder(client, host, url, method, entity.getBytes(), headers, parameters);
	}

	public IHttpClient withEntity(byte[] entity) {
		return new HttpClientBuilder(client, host, url, method, entity, headers, parameters);
	}

	public IHttpClient withParams(String... nameAndValue) {
		assert nameAndValue.length % 2 == 0 : nameAndValue.length + "/" + Arrays.asList(nameAndValue);
		List<NameValuePair> nameAndValues = Lists.newList();
		for (int i = 0; i < nameAndValue.length; i += 2)
			nameAndValues.add(new BasicNameValuePair(nameAndValue[i + 0], nameAndValue[i + 1]));
		return new HttpClientBuilder(client, host, url, method, entity, headers, nameAndValues);
	}

	public IHttpClient addHeader(String name, String value) {
		return new HttpClientBuilder(client, host, url, method, entity, Lists.append(headers, new BasicNameValuePair(name, value)), parameters);
	}

	public IHttpClient addParam(String name, String value) {
		return new HttpClientBuilder(client, host, url, method, entity, headers, Lists.append(parameters, new BasicNameValuePair(name, value)));
	}

}