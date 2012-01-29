/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.httpAccess.requests.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.api.impl.ClientBuilder;
import org.softwareFm.httpClient.requests.IRequestBuilder;
import org.softwareFm.httpClient.requests.impl.AbstractRequestBuilder;

abstract public class AbstractRequestTest extends TestCase {

	protected ClientBuilder builder;

	protected void checkRequest(Class<?> clazz, IRequestBuilder requestBuilder, ClientBuilder builder, String... parameters) {

		assertTrue(clazz.isAssignableFrom(requestBuilder.getClass()));
		AbstractRequestBuilder postOrGet = (AbstractRequestBuilder) requestBuilder;
		assertEquals("/someUrl", postOrGet.url);
		assertEquals(builder.host, postOrGet.host);
		assertEquals(builder.client, postOrGet.client);
		assertEquals(builder.defaultHeaders, postOrGet.defaultHeaders);
		List<NameValuePair> expected = new ArrayList<NameValuePair>();
		for (int i = 0; i < parameters.length; i += 2)
			expected.add(new BasicNameValuePair(parameters[i + 0], parameters[i + 1]));
		assertEquals(expected, postOrGet.parameters);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		builder = (ClientBuilder) IHttpClient.Utils.builder();
		builder.setDefaultHeaders(Arrays.<NameValuePair> asList(new BasicNameValuePair("name", "value")));
	}
}