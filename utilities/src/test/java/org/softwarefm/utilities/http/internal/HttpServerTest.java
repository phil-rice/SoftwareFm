package org.softwarefm.utilities.http.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import junit.framework.TestCase;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.RequestLine;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
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
	private HttpRequestHandler handler1;
	private IHttpServer server;
	private HttpRequest request1;
	protected HttpException httpException;
	protected RuntimeException exception;
	protected String entity2;
	private HttpRequestHandler handler2;
	private List<NameValuePair> foundParameters;
	protected String entity1;

	public void testServerShutdownsNicelyWhenNotStarted() {
	}

	public void testServerShutdownsNicelyWhenStarted() {
		server.start();
	}

	public void testPostParametersAreReceived() {
		server.start();
		client.withParams("a", "1", "b", "2").post("someUrl.xxx").execute();
		Tests.assertEqualsAsSet(foundParameters, new BasicNameValuePair("a", "1"), new BasicNameValuePair("b", "2"));
	}

	public void testInformationSentToServerIsReceived() throws Exception {
		server.start();
		client.post("someUrl.xxx").withEntity("This is the entity1").execute();
		RequestLine requestLine = request1.getRequestLine();
		assertEquals("/someUrl.xxx", requestLine.getUri());
		assertEquals("This is the entity1", entity1);
	}

	public void testInformationInReplyIsReceived() throws Exception {
		server.start();
		IResponse result = client.post("handler1.xxx").execute();
		assertEquals("HereIsResult1", result.asString());
		assertEquals(200, result.statusCode());
	}

	public void testRegisterHandlesMultipleHandlers() {
		server.start();
		IResponse result = client.post("handler1.yyy").execute();
		assertEquals("HereIsResult2", result.asString());
		assertEquals(200, result.statusCode());

	}

	public void testExceptionResultsInStackTraceAndResponseCode() {
		httpException = new HttpException("Some Reason");
		server.start();
		IResponse result = client.post("someUrl.xxx").execute();
		assertEquals("Some Reason", result.asString());
		assertEquals(500, result.statusCode());

	}

	public void testRawExceptionResultsInNoHttpResponseException() {
		exception = new RuntimeException();
		server.start();
		Tests.assertThrows(NoHttpResponseException.class, new Runnable() {
			public void run() {
				client.post("someUrl.xxx").execute();
			}
		});
	}

	public void testExceptionsDontStopNormalUsage() {
		server.start();

		httpException = new HttpException("Some Reason");
		assertEquals(500, client.post("someUrl.xxx").execute().statusCode());
		exception = new RuntimeException();
		Tests.assertThrows(NoHttpResponseException.class, new Runnable() {
			public void run() {
				client.post("someUrl.xxx").execute();
			}
		});
		httpException = null;
		exception = null;

		IResponse result = client.post("someUrl.xxx").execute();
		assertEquals("HereIsResult1", result.asString());
		assertEquals(200, result.statusCode());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		exception = null;
		memory = ICallback.Utils.memory();
		handler1 = new HttpRequestHandler() {
			public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
				HttpServerTest.this.request1 = request;
				if (request instanceof HttpEntityEnclosingRequest) {
					HttpEntity rawEntity = ((HttpEntityEnclosingRequest) request).getEntity();
					entity1 = EntityUtils.toString(rawEntity);
					foundParameters =new ArrayList<NameValuePair>();
					URLEncodedUtils.parse(foundParameters, new Scanner(entity1), "UTF-8");

				}
				response.setEntity(new StringEntity("HereIsResult1"));
				if (exception != null)
					throw exception;
				if (httpException != null)
					throw httpException;
			}
		};
		handler2 = new HttpRequestHandler() {
			public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
				if (request instanceof HttpEntityEnclosingRequest) {
					HttpEntity rawEntity = ((HttpEntityEnclosingRequest) request).getEntity();
					entity2 = EntityUtils.toString(rawEntity);
				}
				response.setEntity(new StringEntity("HereIsResult2"));
			}
		};
		client = IHttpClient.Utils.builder().host("localhost", port);
		server = IHttpServer.Utils.server(1, IHttpServer.Utils.simpleParams("Test"), port, memory);
		server.register("*.xxx", handler1);
		server.register("*.yyy", handler2);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		server.shutdown();
	}

}
