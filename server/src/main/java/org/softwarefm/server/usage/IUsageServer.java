package org.softwarefm.server.usage;

import org.softwarefm.httpServer.IHttpServer;
import org.softwarefm.shared.usage.IUsage;
import org.softwarefm.utilities.callbacks.ICallback;

public interface IUsageServer {

	public static class Utils {
		public static IHttpServer usageServer(int port,ICallback<IUsage> callback,  ICallback<Throwable> exceptionHandler) {
			return IHttpServer.Utils.server(10, IHttpServer.Utils.simpleParams("Software FM Usage Server/1.0"), port, exceptionHandler);
		}
	}
	
	
}
