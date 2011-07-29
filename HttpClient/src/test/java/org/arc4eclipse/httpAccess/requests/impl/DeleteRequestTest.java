package org.arc4eclipse.httpAccess.requests.impl;

import org.arc4eclipse.httpClient.api.IHttpClient;
import org.arc4eclipse.httpClient.api.impl.ClientBuilder;
import org.arc4eclipse.httpClient.requests.impl.DeleteRequest;
import org.junit.Test;

public class DeleteRequestTest extends AbstractRequestTest {

	@Test
	public void testPost() {
		ClientBuilder builder = (ClientBuilder) IHttpClient.Utils.builder();
		checkRequest(DeleteRequest.class, builder.delete("someUrl"), builder);
		checkRequest(DeleteRequest.class, builder.delete("/someUrl"), builder);
	}

}
