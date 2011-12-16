package org.softwareFm.server;

import org.softwareFm.server.internal.SoftwareFmServer;
import org.softwareFm.utilities.callbacks.ICallback;

public interface ISoftwareFmServer {

	public void shutdown();
	

	public static class Utils {
		public static ISoftwareFmServer serverPort8080(IProcessCall processCall, ICallback<Throwable> errorHandler) {
			return server(8080, 2, processCall, errorHandler);
		}

		public static ISoftwareFmServer server(int port, int threads, IProcessCall processCall, ICallback<Throwable> errorHandler) {
			return new SoftwareFmServer(port, threads, processCall, errorHandler);
		}

	}
}
