package org.softwareFm.server.internal;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.softwareFm.httpClient.api.IClientBuilder;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.requests.CheckCallback;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ISoftwareFmServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.AbstractLoginDataAccessor;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.callbacks.MemoryCallback;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.runnable.Callables;
import org.softwareFm.utilities.tests.IIntegrationTest;
import org.springframework.core.io.ClassPathResource;

public class SoftwareFmServerIntegrationTest extends TestCase implements IIntegrationTest {
	private MemoryCallback<Throwable> memory;
	private ISoftwareFmServer server;
	private IClientBuilder client;

	public void testThrows404ForContentXml() throws Exception {
		CheckCallback checkCallback = IResponseCallback.Utils.checkCallback(ServerConstants.notFoundStatusCode, "");
		client.head("content.xml").execute(checkCallback).get(200, TimeUnit.SECONDS);
		checkCallback.assertCalledSuccessfullyOnce();
	}

	public void testReturnsStyleTypeForFiles() throws Exception {
		client.get("test.css").execute(new IResponseCallback() {
			@Override
			public void process(IResponse response) {
				assertEquals(ServerConstants.okStatusCode, response.statusCode());
				assertEquals("Here is some content", response.asString());
				assertEquals("text/css", response.mimeType());
			}
		}).get(2000, TimeUnit.SECONDS);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		memory = ICallback.Utils.memory();
		File fileRoot = new ClassPathResource("test.css", getClass()).getFile().getParentFile();
		IGitServer gitServer = IGitServer.Utils.noGitServer();
		IFunction1<Map<String, Object>, String> cryptoFn = Functions.expectionIfCalled();
		Callable<String> monthGetter = Callables.exceptionIfCalled();
		Callable<Integer> dayGetter = Callables.exceptionIfCalled();
		Callable<String> softwareFmIdGenerator = Callables.exceptionIfCalled();
		Callable<String> cryptoGenerator = Callables.exceptionIfCalled();
		server = ISoftwareFmServer.Utils.testServerPort(IProcessCall.Utils.softwareFmProcessCallWithoutMail(AbstractLoginDataAccessor.defaultDataSource(), gitServer, cryptoFn, cryptoGenerator, fileRoot, monthGetter, dayGetter, softwareFmIdGenerator), memory);
		client = IHttpClient.Utils.builder("localhost", ServerConstants.testPort);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		server.shutdown();
	}
}
