package org.softwareFm.httpAccess.requests.impl;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.softwareFm.httpClient.api.impl.ClientBuilder;
import org.softwareFm.httpClient.requests.IRequestBuilder;
import org.softwareFm.httpClient.requests.impl.AbstractRequestBuilder;

abstract public class AbstractRequestTest extends TestCase {

	protected void checkRequest(Class<?> clazz, IRequestBuilder requestBuilder, ClientBuilder builder, String... parameters) {
		assertTrue(clazz.isAssignableFrom(requestBuilder.getClass()));
		AbstractRequestBuilder postOrGet = (AbstractRequestBuilder) requestBuilder;
		assertEquals("/someUrl", postOrGet.url);
		assertEquals(builder.host, postOrGet.host);
		assertEquals(builder.client, postOrGet.client);
		List<NameValuePair> expected = new ArrayList<NameValuePair>();
		for (int i = 0; i < parameters.length; i += 2)
			expected.add(new BasicNameValuePair(parameters[i + 0], parameters[i + 1]));
		assertEquals(expected, postOrGet.parameters);
	}

}
