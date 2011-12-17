package org.softwareFm.server;

import org.softwareFm.server.internal.SoftwareFmServer;
import org.softwareFm.utilities.callbacks.ICallback;

public interface ISoftwareFmServer {

	public void shutdown();

	public static class Utils {
		public static ISoftwareFmServer testServerPort(IProcessCall processCall, ICallback<Throwable> errorHandler) {
			return server(ServerConstants.testPort, 2, processCall, errorHandler);
		}

		public static ISoftwareFmServer server(int port, int threads, IProcessCall processCall, ICallback<Throwable> errorHandler) {
			return new SoftwareFmServer(port, threads, processCall, errorHandler);
		}

	}
}