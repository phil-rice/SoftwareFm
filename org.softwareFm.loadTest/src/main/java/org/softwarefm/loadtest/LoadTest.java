package org.softwarefm.loadtest;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.http.RequestLine;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ISoftwareFmServer;
import org.softwareFm.server.processors.AbstractLoginDataAccessor;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.runnable.Callables;

public class LoadTest {

	public static void main(String[] args) {
		File root = new File(System.getProperty("user.home"));
		File sfmRoot = new File(root, ".sfm_remote");
		IGitServer server = IGitServer.Utils.gitServer(sfmRoot, "not used");
		IFunction1<Map<String, Object>, String> cryptoFn = Functions.expectionIfCalled();
		Callable<String> monthGetter = Callables.exceptionIfCalled();
		Callable<Integer> dayGetter = Callables.exceptionIfCalled();
		Callable<String> cryptoGenerator = Callables.exceptionIfCalled();
		final IProcessCall rawProcessCall = IProcessCall.Utils.softwareFmProcessCallWithoutMail(AbstractLoginDataAccessor.defaultDataSource(), server, cryptoFn, cryptoGenerator, sfmRoot, monthGetter, dayGetter, Callables.<String> exceptionIfCalled(), "g", "a");
		IProcessCall processCall = new IProcessCall() {
			@Override
			public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
				long startTime = System.nanoTime();
				try {
					return rawProcessCall.process(requestLine, parameters);
				} finally {
					long duration = System.nanoTime() - startTime;
					System.out.println(requestLine.getUri() + ": " + duration / 1000000);
				}
			}
		};
		ISoftwareFmServer.Utils.testServerPort(processCall, ICallback.Utils.sysErrCallback());
	}

}
