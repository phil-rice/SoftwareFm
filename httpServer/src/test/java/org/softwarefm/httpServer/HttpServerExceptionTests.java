package org.softwarefm.httpServer;

import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.softwarefm.httpServer.routes.RouteHandlerMock;
import org.softwarefm.utilities.exceptions.Exceptions;
import org.softwarefm.utilities.http.HttpMethod;
import org.softwarefm.utilities.http.IResponse;

public class HttpServerExceptionTests extends AbstractHttpServerTest {

	private RouteHandlerMock handler;

	public void testExceptionReturn500AndDoesntStopOtherThingsWorking(){
		httpServer.register(HttpMethod.POST, handler, "*");
		httpServer.start();
		checkException(new RuntimeException());
		checkException(new AssertionError());
		checkException(new HttpException());
	}
	
	
	private void checkException(Throwable t) {
		handler.setThrowable(t);
		IResponse response = httpClient.post("anyurl").execute();
		assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.statusCode());
		assertEquals(Exceptions.serialize(t, "<br>\n"), response.asString());
		
		checkStillWorks();
	}


	private void checkStillWorks() {
		handler.setThrowable(null);
		callAndCheck(HttpMethod.POST, "anyurl", handler);
	}


	@Override
	protected void setUp() throws Exception {
		super.setUp();
		handler = new RouteHandlerMock("handlerA1", HttpStatus.SC_OK);
	}

}
