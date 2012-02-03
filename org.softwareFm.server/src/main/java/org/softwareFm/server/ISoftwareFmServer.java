package org.softwareFm.server;

import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.server.internal.SoftwareFmServer;
import org.softwareFm.server.processors.IProcessCall;

public interface ISoftwareFmServer {

	public void shutdown();

	abstract public static class Utils {
		public static ISoftwareFmServer testServerPort(IProcessCall processCall, ICallback<Throwable> errorHandler) {
			return server(CommonConstants.testPort, 2, processCall, errorHandler);
		}

		public static ISoftwareFmServer server(int port, int threads, IProcessCall processCall, ICallback<Throwable> errorHandler) {
			return new SoftwareFmServer(port, threads, processCall, errorHandler, IUsage.Utils.defaultUsage());
		}
		

	}
}