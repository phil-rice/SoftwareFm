package org.arc4eclipse.httpAccess.requests.impl;

import org.arc4eclipse.httpClient.api.IHttpClient;
import org.arc4eclipse.httpClient.api.impl.ClientBuilder;
import org.arc4eclipse.httpClient.requests.impl.DeleteRequest;

public class DeleteRequestTest extends AbstractRequestTest {

	public void testPost() {
		ClientBuilder builder = (ClientBuilder) IHttpClient.Utils.builder();
		DeleteRequest post = (DeleteRequest) builder.delete("someUrl");
		checkRequest(post, builder);
	}

}
