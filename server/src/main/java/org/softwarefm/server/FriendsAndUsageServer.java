package org.softwarefm.server;

import org.softwarefm.eclipse.usage.IUsagePersistance;
import org.softwarefm.server.friend.internal.FriendServerHandler;
import org.softwarefm.server.usage.internal.MysqlTestData;
import org.softwarefm.server.usage.internal.UsageMysqlCallback;
import org.softwarefm.server.usage.internal.UsageServerHandler;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.http.IHttpServer;
import org.softwarefm.utilities.time.ITime;

public class FriendsAndUsageServer {

	public static void main(String[] args) throws InterruptedException {
		IHttpServer server = IHttpServer.Utils.server(10, IHttpServer.Utils.simpleParams("Friends and Usage Server/1.0"), 80, ICallback.Utils.sysErrCallback());
		IUsagePersistance persistance = IUsagePersistance.Utils.persistance();
		UsageMysqlCallback usageMysqlCallback = new UsageMysqlCallback(MysqlTestData.dataSource, ITime.Utils.system());
		server.register("usage*", new UsageServerHandler(persistance, usageMysqlCallback));
		server.register("user*", new FriendServerHandler(persistance, null));
		server.start();
		while (true) {
			Thread.sleep(1000000);
		}
	}

}
