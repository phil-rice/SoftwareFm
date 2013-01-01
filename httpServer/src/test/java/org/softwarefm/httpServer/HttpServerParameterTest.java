package org.softwarefm.httpServer;

import org.apache.http.HttpStatus;
import org.softwarefm.utilities.http.HttpMethod;

public class HttpServerParameterTest extends AbstractHttpServerTest {
	private RouteHandlerWithParametersMock handlerWithParameters;

	public void testExceptionifParametersAreDuplicated() {
		httpServer.register(HttpMethod.POST, handlerWithParameters, "prefix/{0}", "c");
		httpServer.start();
		httpClient = httpClient.withParams("a", "1", "b", "2", "c", "3");
		String error = checkCall(httpClient.post("prefix/3"), HttpStatus.SC_INTERNAL_SERVER_ERROR);
		assertTrue(error, error.contains("Cannot add c with value 3"));
		assertEquals(0, handlerWithParameters.getCount());
	}

	public void testParametersArePassedIfIRouteHandlerWithParametersInterfaceIsPresent() {
		httpServer.register(HttpMethod.POST, handlerWithParameters, "*");
		httpServer.start();
		httpClient = httpClient.withParams("a", "1", "b", "2");
		callAndCheck(HttpMethod.POST, "someUrl", handlerWithParameters, "a", "1", "b", "2");
		// callAndCheck(HttpMethod.PUT, "someUrl", handlerWithParameters, "a", "1", "b", "2");
	}

	public void testParametersAreMixed() {
		httpServer.register(HttpMethod.POST, handlerWithParameters, "prefix/{0}", "c");
		httpServer.start();
		httpClient = httpClient.withParams("a", "1", "b", "2");
		callAndCheck(HttpMethod.POST, "prefix/3", handlerWithParameters, "a", "1", "b", "2", "c", "3");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		handlerWithParameters = new RouteHandlerWithParametersMock("handlerWithParameters", HttpStatus.SC_OK);
	}
}
