package org.arc4eclipse.httpAccess.requests.impl;

import org.arc4eclipse.httpClient.api.IHttpClient;
import org.arc4eclipse.httpClient.api.impl.ClientBuilder;
import org.arc4eclipse.httpClient.requests.impl.PostRequest;

public class PostRequestTest extends AbstractRequestTest {
	public void testPost() {
		ClientBuilder builder = (ClientBuilder) IHttpClient.Utils.builder();
		PostRequest post = (PostRequest) builder.post("someUrl");
		checkRequest(post, builder);
	}

	public void testPostWithParameters() {
		ClientBuilder builder = (ClientBuilder) IHttpClient.Utils.builder();
		checkRequest(builder.post("someUrl"), builder);
		checkRequest(builder.post("someUrl").addParam("n1", "v1"), builder, "n1", "v1");
		checkRequest(builder.post("someUrl").addParam("n1", "v1").addParam("n2", "v2"), builder, "n1", "v1", "n2", "v2");
		checkRequest(builder.post("someUrl").addParam("n1", "v1").addParams("n2", "v2", "n3", "v3"), builder, "n1", "v1", "n2", "v2", "n3", "v3");
	}
}
