/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.utilities.http;

import java.util.Collections;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.softwarefm.utilities.http.internal.HttpClientBuilder;

/** This is the prototype pattern with immutables: the methods return new objects, and the old object is unaffected by the methods */
public interface IHttpClient {

	/** Throws an exception if not ready to execute */
	void validate() throws HttpClientValidationException;

	IHttpClient host(String host);

	IHttpClient host(String host, int port);

	IHttpClient post(String urlPattern, Object...params);

	IHttpClient put(String urlPattern, Object...params);

	IHttpClient get(String urlPattern, Object...params);

	IHttpClient head(String urlPattern, Object...params);

	IHttpClient delete(String urlPattern, Object...params);

	IHttpClient method(HttpMethod method, String urlPattern, Object...params);

	IHttpClient addHeader(String name, String value);

	IHttpClient addParam(String name, String value);

	IHttpClient withParameters(List<NameValuePair> nameAndValues);

	IHttpClient withParams(String... nameAndValue);

	IHttpClient withEntity(String entity);

	IHttpClient withEntity(byte[] entity);

	IResponse execute();

	public static class Utils {
		public static IHttpClient builder() {
			return new HttpClientBuilder(new DefaultHttpClient(new ThreadSafeClientConnManager()), null, null, null, null, null, Collections.<NameValuePair> emptyList());
		}

		public static boolean headerEquals(HttpResponse response, String name, String value) {
			Header[] headers = response.getHeaders(name);
			if (headers != null)
				for (Header header : headers)
					if (value.equals(header.getValue()))
						return true;
			return false;
		}

		public static boolean headerEquals(HttpRequest request, String name, String value) {
			Header[] headers = request.getHeaders(name);
			if (headers != null)
				for (Header header : headers)
					if (value.equals(header.getValue()))
						return true;
			return false;
		}
	}
}