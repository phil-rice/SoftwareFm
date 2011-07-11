package org.arc4eclipse.httpAccess.requests.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.arc4eclipse.httpClient.api.impl.ClientBuilder;
import org.arc4eclipse.httpClient.requests.IRequestBuilder;
import org.arc4eclipse.httpClient.requests.impl.AbstractRequestBuilder;

public class AbstractRequestTest {

	protected void checkRequest(IRequestBuilder requestBuilder, ClientBuilder builder, String... parameters) {
		AbstractRequestBuilder postOrGet = (AbstractRequestBuilder) requestBuilder;
		assertEquals("someUrl", postOrGet.url);
		assertEquals(builder.host, postOrGet.host);
		assertEquals(builder.client, postOrGet.client);
		List<NameValuePair> expected = new ArrayList<NameValuePair>();
		for (int i = 0; i < parameters.length; i += 2)
			expected.add(new BasicNameValuePair(parameters[i + 0], parameters[i + 1]));
		assertEquals(expected, postOrGet.parameters);
	}

}
