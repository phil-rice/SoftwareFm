package org.softwareFm.server.internal;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.apache.http.RequestLine;
import org.softwareFm.httpClient.api.IClientBuilder;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.httpClient.requests.MemoryResponseCallback;
import org.softwareFm.server.ISoftwareFmServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.callbacks.MemoryCallback;
import org.softwareFm.utilities.comparators.Comparators;
import org.softwareFm.utilities.maps.Maps;

public class SoftwareFmServerTest extends TestCase {

	private ISoftwareFmServer server;
	private MemoryCallback<Throwable> memory;
	private IClientBuilder client;

	public void testServerAppliesProcessCallAndReturnsResult() throws Exception {
		checkGet();
	}

	public void testServerDoesntRespondAfterShutdown() throws InterruptedException {
		MemoryResponseCallback<Object, Object> memoryCallback = IResponseCallback.Utils.memoryCallback();
		server.shutdown();
		client.get("someUrl").execute(memoryCallback);
		Thread.sleep(10);// This would have been long enough for the server to execute memory...but it isnt going to
		assertEquals(0, memoryCallback.count.get());
	}

	public void testExceptionsDealtWithByExceptionHandler() throws Exception {
		checkExceptionHandled();
	}

	public void testExceptionsDontUseUpThreads() throws Exception {
		// note there are only 2 threadsin the server
		checkExceptionHandled();
		checkExceptionHandled();
		checkExceptionHandled();
		checkExceptionHandled();
		checkExceptionHandled();
		checkExceptionHandled();
		checkExceptionHandled();
		checkExceptionHandled();
		checkExceptionHandled();
		checkExceptionHandled();
		checkExceptionHandled();
		checkExceptionHandled();
		checkExceptionHandled();
		checkExceptionHandled();
		checkExceptionHandled();
		checkGet();
		checkGet();
	}

	private void checkGet() throws Exception {
		MemoryResponseCallback<Object, Object> memoryCallback = IResponseCallback.Utils.memoryCallback();
		client.get("someUrl").execute(memoryCallback).get(1, TimeUnit.SECONDS);
		assertEquals("Response [url=/someUrl, statusCode=200, string=<GET /someUrl HTTP/1.1,{}>]", memoryCallback.response.toString());
	}

	private void checkExceptionHandled() throws Exception {
		MemoryResponseCallback<Object, Object> memoryCallback = IResponseCallback.Utils.memoryCallback();
		client.get("exception").execute(memoryCallback).get(2, TimeUnit.SECONDS);
		assertEquals("Response [url=/exception, statusCode=500, string=class java.lang.RuntimeException/null]", memoryCallback.response.toString());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		memory = ICallback.Utils.memory();
		server = ISoftwareFmServer.Utils.testServerPort(new ProcessCallMock(), memory);
		client = IHttpClient.Utils.builder("localhost", ServerConstants.testPort);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		server.shutdown();
	}

	static class ProcessCallMock implements IProcessCall {

		@Override
		public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
			if (requestLine.toString().contains("exception"))
				throw new RuntimeException();
			return IProcessResult.Utils.processString("<" + requestLine + "," + Maps.sortByKey(parameters, Comparators.<String> naturalOrder()) + ">");
		}
	}

}
