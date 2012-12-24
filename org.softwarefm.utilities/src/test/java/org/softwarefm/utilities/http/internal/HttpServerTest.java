package org.softwarefm.utilities.http.internal;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.RequestLine;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.callbacks.MemoryCallback;
import org.softwarefm.utilities.http.IHttpClient;
import org.softwarefm.utilities.http.IHttpServer;
import org.softwarefm.utilities.http.IResponse;
import org.softwarefm.utilities.tests.Tests;

public class HttpServerTest extends TestCase {
	private static final int port = 8097;
	private IHttpClient client;
	private MemoryCallback<Throwable> memory;
	private HttpRequestHandler handler;
	private IHttpServer server;
	private HttpRequest request;
	private HttpResponse response;
	protected String entity;
	protected HttpException httpException;
	protected RuntimeException exception;

	public void testServerShutdownsNicelyWhenNotStarted() {
	}

	public void testServerShutdownsNicelyWhenStarted() {
		server.start();
	}

	public void testInformationSentToServerIsReceived() throws Exception {
		server.start();
		client.post("someUrl").withEntity("This is the entity").execute();
		RequestLine requestLine = request.getRequestLine();
		assertEquals("/someUrl", requestLine.getUri());
		assertEquals("This is the entity", entity);
	}

	public void testInformationInReplyIsReceived() throws Exception {
		server.start();
		IResponse result = client.post("someUrl").execute();
		assertEquals("HereIsResult", result.asString());
		assertEquals(200, result.statusCode());
	}

	public void testExceptionResultsInStackTraceAndResponseCode() {
		httpException = new HttpException("Some Reason");
		server.start();
		IResponse result = client.post("someUrl").execute();
		assertEquals("Some Reason", result.asString());
		assertEquals(500, result.statusCode());

	}

	public void testRawExceptionResultsInNoHttpResponseException() {
		exception = new RuntimeException();
		server.start();
		Tests.assertThrows(NoHttpResponseException.class, new Runnable() {
			public void run() {
				client.post("someUrl").execute();
			}
		});
	}

	public void testExceptionsDontStopNormalUsage() {
		server.start();

		httpException = new HttpException("Some Reason");
		assertEquals(500, client.post("someUrl").execute().statusCode());
		exception = new RuntimeException();
		Tests.assertThrows(NoHttpResponseException.class, new Runnable() {
			public void run() {
				client.post("someUrl").execute();
			}
		});
		httpException = null;
		exception = null;
		
		IResponse result = client.post("someUrl").execute();
		assertEquals("HereIsResult", result.asString());
		assertEquals(200, result.statusCode());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		exception = null;
		memory = ICallback.Utils.memory();
		handler = new HttpRequestHandler() {
			public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
				HttpServerTest.this.request = request;
				HttpServerTest.this.response = response;
				if (request instanceof HttpEntityEnclosingRequest) {
					HttpEntity rawEntity = ((HttpEntityEnclosingRequest) request).getEntity();
					entity = EntityUtils.toString(rawEntity);
				}
				response.setEntity(new StringEntity("HereIsResult"));
				if (exception != null)
					throw exception;
				if (httpException != null)
					throw httpException;
			}
		};
		client = IHttpClient.Utils.builder().host("localhost", port);
		server = IHttpServer.Utils.server(1, IHttpServer.Utils.simpleParams("Test"), port, handler, memory);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		server.shutdown();
	}

}
