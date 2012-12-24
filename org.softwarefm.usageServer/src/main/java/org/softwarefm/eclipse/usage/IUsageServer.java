package org.softwarefm.eclipse.usage;

import org.softwarefm.eclipse.usage.internal.UsageServerHandler;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.http.IHttpServer;

public interface IUsageServer {

	public static class Utils {
		public static IHttpServer usageServer(int port,ICallback<IUsage> callback,  ICallback<Throwable> exceptionHandler) {
			return IHttpServer.Utils.server(10, IHttpServer.Utils.simpleParams("Software FM Usage Server/1.0"), port, new UsageServerHandler(callback), exceptionHandler);
		}
	}
	
	
}
