package org.softwarefm.httpServer;

import java.util.Map;

import junit.framework.TestCase;

import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.softwarefm.httpServer.routes.RouteHandlerMock;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.http.HttpMethod;
import org.softwarefm.utilities.maps.Maps;

/** Checks that the back doors into the server work. i.e. we can use without having to go over a socket */
public class HttpServerProcessTests extends TestCase {
	private RouteHandlerMock handlerA1;
	private RouteHandlerMock handlerA2;
	private RouteHandlerMock handlerB;
	private IHttpServer httpServer;

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

	private void callAndCheck(HttpMethod method, String uri, RouteHandlerMock handler, int i, String... params) {
		try {
			StatusAndEntity process = IHttpServer.Utils.process(httpServer, method, uri, params);
			String responseString = EntityUtils.toString(process.entity);
			if (method == HttpMethod.HEAD)
				assertEquals("", responseString);
			else
				assertEquals(handler.responseString, responseString);
			assertEquals(handler.statusCode, process.status);
			assertEquals(method, handler.method);
			Map<String, String> expected = Maps.<String, String> makeMap((Object[]) params);
			assertEquals(expected, handler.parameters);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		httpServer = IHttpServer.Utils.server(1, IHttpServer.Utils.simpleParams("test"), 8000, ICallback.Utils.sysErrCallback());
		handlerA1 = new RouteHandlerMock("handlerA1", HttpStatus.SC_OK);
		handlerA2 = new RouteHandlerMock("handlerA2", HttpStatus.SC_ACCEPTED);
		handlerB = new RouteHandlerMock("handlerB", HttpStatus.SC_CONFLICT);
	}
}
