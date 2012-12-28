package org.softwarefm.server;

import javax.sql.DataSource;

import org.softwarefm.httpServer.IHttpServer;
import org.softwarefm.server.configurator.FriendsConfigurator;
import org.softwarefm.server.configurator.UsageConfigurator;
import org.softwarefm.server.friend.internal.FriendManagerServer;
import org.softwarefm.server.usage.internal.UsageMysqlCallbackAndGetter;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.time.ITime;

public class SoftwareFmServer {
	
	
	public SoftwareFmServer(DataSource dataSource, int port) {
		IHttpServer httpServer = IHttpServer.Utils.server(10, IHttpServer.Utils.simpleParams("SoftwareFmUsageAndFriendsServer/1.0"), port, ICallback.Utils.sysErrCallback());
		DataSource datasource = MysqlTestData.dataSource;
		UsageMysqlCallbackAndGetter callbackAndGetter = new UsageMysqlCallbackAndGetter(datasource, ITime.Utils.system());
		FriendManagerServer friendManager = new FriendManagerServer(datasource);
		IUsagePersistance persistance = IUsagePersistance.Utils.persistance();
		httpServer.configure(new UsageConfigurator(callbackAndGetter, callbackAndGetter, persistance ));
		httpServer.configure(new FriendsConfigurator(friendManager, persistance));
		httpServer.start();	}

	public static void main(String[] args) throws InterruptedException {
		new SoftwareFmServer(MysqlTestData.dataSource, 8082);
		while(true)
			Thread.sleep(100000);
	}
}
