package org.softwareFm.httpAccess.requests.impl;

import org.junit.Test;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.api.impl.ClientBuilder;
import org.softwareFm.httpClient.requests.impl.GetRequest;

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
