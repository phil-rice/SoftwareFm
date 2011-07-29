package org.arc4eclipse.httpAccess.requests.impl;

import org.arc4eclipse.httpClient.api.IHttpClient;
import org.arc4eclipse.httpClient.api.impl.ClientBuilder;
import org.arc4eclipse.httpClient.requests.impl.GetRequest;
import org.junit.Test;

public class GetRequestTest extends AbstractRequestTest {
	@Test
	public void testGet() {
		ClientBuilder builder = (ClientBuilder) IHttpClient.Utils.builder();
		checkRequest(GetRequest.class, builder.get("someUrl"), builder);
		checkRequest(GetRequest.class, builder.get("/someUrl"), builder);
	}

	@Test
	public void testGetWithParameters() {
		ClientBuilder builder = (ClientBuilder) IHttpClient.Utils.builder();
		checkRequest(GetRequest.class, builder.get("someUrl"), builder);
		checkRequest(GetRequest.class, builder.get("someUrl").addParam("n1", "v1"), builder, "n1", "v1");
		checkRequest(GetRequest.class, builder.get("someUrl").addParam("n1", "v1").addParam("n2", "v2"), builder, "n1", "v1", "n2", "v2");
		checkRequest(GetRequest.class, builder.get("someUrl").addParam("n1", "v1").addParams("n2", "v2", "n3", "v3"), builder, "n1", "v1", "n2", "v2", "n3", "v3");
		checkRequest(GetRequest.class, builder.get("/someUrl").addParam("n1", "v1").addParams("n2", "v2", "n3", "v3"), builder, "n1", "v1", "n2", "v2", "n3", "v3");
	}
}
