package org.softwarefm.loadtest;

import java.io.File;
import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ISoftwareFmServer;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.utilities.callbacks.ICallback;

public class LoadTest {

	public static void main(String[] args) {
		File root = new File(System.getProperty("user.home"));
		File sfmRoot = new File(root, ".sfm_remote");
		IGitServer server = IGitServer.Utils.gitServer(sfmRoot, "not used");
		final IProcessCall rawProcessCall = IProcessCall.Utils.softwareFmProcessCall(server, sfmRoot);
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
