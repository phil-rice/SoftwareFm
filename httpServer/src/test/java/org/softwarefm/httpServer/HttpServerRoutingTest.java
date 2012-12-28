package org.softwarefm.httpServer;

import org.apache.http.HttpStatus;
import org.softwarefm.httpServer.routes.RouteHandlerMock;
import org.softwarefm.utilities.http.HttpMethod;

public class HttpServerRoutingTest extends AbstractHttpServerTest {

	private RouteHandlerMock handlerA1;
	private RouteHandlerMock handlerA2;
	private RouteHandlerMock handlerB;

	public void testStarMapsEverything() {
		for (HttpMethod method : HttpMethod.values())
			httpServer.register(method, handlerA1, "*");
		httpServer.start();
		callAndCheck(HttpMethod.GET, "anything", handlerA1, 1);
		callAndCheck(HttpMethod.POST, "anything", handlerA1, 2);
		callAndCheck(HttpMethod.DELETE, "anything", handlerA1, 3);
		callAndCheck(HttpMethod.PUT, "anything", handlerA1, 4);
		callAndCheck(HttpMethod.HEAD, "anything", handlerA1, 5);
	}

	public void testCallsHandlerThatMatches() {
		httpServer.register(HttpMethod.GET, handlerA1, "a/{0}", "id");
		httpServer.register(HttpMethod.GET, handlerA2, "a/{0}", "id");
		httpServer.register(HttpMethod.GET, handlerB, "b/{0}", "id");
		httpServer.start();

		callAndCheck(HttpMethod.GET, "a/1", handlerA1, 1, "id", "1");
		callAndCheck(HttpMethod.GET, "a/2", handlerA1, 2, "id", "2");
		callAndCheck(HttpMethod.GET, "b/1", handlerB, 1, "id", "1");
		callAndCheck(HttpMethod.GET, "a/3", handlerA1, 3, "id", "3");
		assertEquals(0, handlerA2.getCount());// never called because a1 always called
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		handlerA1 = new RouteHandlerMock("handlerA1", HttpStatus.SC_OK);
		handlerA2 = new RouteHandlerMock("handlerA2", HttpStatus.SC_ACCEPTED);
		handlerB = new RouteHandlerMock("handlerB", HttpStatus.SC_CONFLICT);
	}
}
