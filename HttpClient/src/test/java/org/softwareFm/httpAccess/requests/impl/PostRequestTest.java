package org.softwareFm.httpAccess.requests.impl;

import org.junit.Test;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.api.impl.ClientBuilder;
import org.softwareFm.httpClient.requests.impl.PostRequest;

public class PostRequestTest extends AbstractRequestTest {
	@Test
	public void testPost() {
		ClientBuilder builder = (ClientBuilder) IHttpClient.Utils.builder();
		checkRequest(PostRequest.class, builder.post("someUrl"), builder);
		checkRequest(PostRequest.class, builder.post("/someUrl"), builder);
	}

	@Test
	public void testPostWithParameters() {
		ClientBuilder builder = (ClientBuilder) IHttpClient.Utils.builder();
		checkRequest(PostRequest.class, builder.post("someUrl"), builder);
		checkRequest(PostRequest.class, builder.post("someUrl").addParam("n1", "v1"), builder, "n1", "v1");
		checkRequest(PostRequest.class, builder.post("someUrl").addParam("n1", "v1").addParam("n2", "v2"), builder, "n1", "v1", "n2", "v2");
		checkRequest(PostRequest.class, builder.post("someUrl").addParam("n1", "v1").addParams("n2", "v2", "n3", "v3"), builder, "n1", "v1", "n2", "v2", "n3", "v3");
		checkRequest(PostRequest.class, builder.post("/someUrl").addParam("n1", "v1").addParams("n2", "v2", "n3", "v3"), builder, "n1", "v1", "n2", "v2", "n3", "v3");
	}
}
