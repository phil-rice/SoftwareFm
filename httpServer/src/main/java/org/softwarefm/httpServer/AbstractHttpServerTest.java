package org.softwarefm.httpServer;

import java.util.Map;

import junit.framework.TestCase;

import org.apache.http.HttpStatus;
import org.softwarefm.httpServer.routes.RouteHandlerMock;
import org.softwarefm.utilities.callbacks.MemoryCallback;
import org.softwarefm.utilities.http.HttpMethod;
import org.softwarefm.utilities.http.IHttpClient;
import org.softwarefm.utilities.http.IResponse;
import org.softwarefm.utilities.maps.Maps;

public abstract class AbstractHttpServerTest extends TestCase {

	protected final int port = 8190;
	protected IHttpServer httpServer;
	protected MemoryCallback<Throwable> memoryThrowable;
	protected IHttpClient httpClient;

	protected String checkCall(IHttpClient client) {
		IResponse response = client.execute();
		assertEquals(HttpStatus.SC_OK, response.statusCode());
		return response.asString();
	}

	protected String checkCall(IHttpClient client, int statusCode) {
		IResponse response = client.execute();
		assertEquals(statusCode, response.statusCode());
		return response.asString();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		memoryThrowable = new MemoryCallback<Throwable>();
		httpServer = IHttpServer.Utils.server(2, IHttpServer.Utils.simpleParams("test server"), port, memoryThrowable);
		httpClient = IHttpClient.Utils.builder().host("localhost", port);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (httpServer != null)
			httpServer.shutdown();
		memoryThrowable.assertNotCalled();
	}

	protected void callAndCheck(String url, RouteHandlerMock handler, String... expectedParameters) {
		for (HttpMethod method : HttpMethod.values())
			callAndCheck(method, url, handler, expectedParameters);
	}

	protected void callAndCheck(HttpMethod method, String url, RouteHandlerMock handler, String... expectedParameters) {
		IResponse response = httpClient.method(method, url).execute();
		if (method == HttpMethod.HEAD)
			assertEquals("", response.asString());
		else
			assertEquals(handler.responseString, response.asString());
		assertEquals(handler.statusCode, response.statusCode());
		assertEquals(method, handler.method);
		Map<String, String> expected = Maps.<String, String> makeMap((Object[]) expectedParameters);
		assertEquals(expected, handler.parameters);
	}

	protected void callAndCheck(HttpMethod method, String url, RouteHandlerMock handler, int expectedCount, String... expectedParameters) {
		callAndCheck(method, url, handler, expectedParameters);
		assertEquals(expectedCount, handler.getCount());

	}
}
