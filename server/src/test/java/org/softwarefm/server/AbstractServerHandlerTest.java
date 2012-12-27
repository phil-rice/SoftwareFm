package org.softwarefm.server;

import junit.framework.TestCase;

import org.apache.http.protocol.HttpRequestHandler;
import org.softwarefm.utilities.callbacks.MemoryCallback;
import org.softwarefm.utilities.http.IHttpClient;
import org.softwarefm.utilities.http.IHttpServer;

public abstract class AbstractServerHandlerTest<H extends HttpRequestHandler> extends TestCase {

	abstract protected H createHandler();

	protected final int port = 8190;
	protected IHttpServer httpServer;
	protected MemoryCallback<Throwable> memoryThrowable;
	protected H handler;
	protected IHttpClient httpClient;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		memoryThrowable = new MemoryCallback<Throwable>();
		httpServer = IHttpServer.Utils.server(2, IHttpServer.Utils.simpleParams("test server"), port, memoryThrowable);
		httpServer.register("*", handler = createHandler());
		httpClient = IHttpClient.Utils.builder().host("localhost", port);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		memoryThrowable.assertNotCalled();
		if (httpServer != null)
			httpServer.shutdown();
	}
}
