package org.softwarefm.loadtest;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.http.RequestLine;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ISoftwareFmServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.AbstractLoginDataAccessor;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.runnable.Callables;

public class TimedServer {

	public static void main(String[] args) throws Exception {
		File root = new File(System.getProperty("user.home"));
		File sfmRoot = new File(root, ".sfm_remote");
		IGitServer server = IGitServer.Utils.gitServer(sfmRoot, "not used");
		IFunction1<Map<String, Object>, String> cryptoFn = Functions.expectionIfCalled();
		Callable<String> monthGetter = Callables.exceptionIfCalled();
		Callable<Integer> dayGetter = Callables.exceptionIfCalled();
		final IProcessCall rawProcessCall = IProcessCall.Utils.softwareFmProcessCallWithoutMail(AbstractLoginDataAccessor.defaultDataSource(), server, cryptoFn, sfmRoot, monthGetter, dayGetter, Callables.<String> exceptionIfCalled());
		IProcessCall processCall = new IProcessCall() {
			@Override
			public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
				long startTime = System.nanoTime();
				try {
					return rawProcessCall.process(requestLine, parameters);
				} finally {
					@SuppressWarnings("unused")
					long duration = System.nanoTime() - startTime;
					// System.out.println(requestLine.getUri() + ": " + duration / 1000000.0);
				}
			}
		};
		int threads = 10;
		final int callsPerThread = 100;
		final String host = "localhost";
		ISoftwareFmServer softwareFmServer = host.equals("localhost") ? ISoftwareFmServer.Utils.server(ServerConstants.testPort, 10 * threads, processCall, ICallback.Utils.<Throwable> noCallback()) : null;
		MultiThreadedTimeTester<IHttpClient> tester = new MultiThreadedTimeTester<IHttpClient>(threads, callsPerThread, new ITimable<IHttpClient>() {
			@Override
			public IHttpClient start(int thread) {
				IHttpClient client = IHttpClient.Utils.builderWithThreads(host, ServerConstants.testPort, 10);
				return client;
			}

			@Override
			public void execute(IHttpClient context, int thread, int index) throws Exception {
				Future<?> future = context.get("/softwareFm/data/org/acegisecurity/org.acegisecurity/").//
						execute(IResponseCallback.Utils.noCallback());
				future.get();
			}

			@Override
			public void finished(IHttpClient context, int thread) {
				context.shutdown();
			}

		});
		tester.testMe();
		if (softwareFmServer != null)
			softwareFmServer.shutdown();
		Thread.sleep(1000);
		tester.dumpStats();
	}

}
