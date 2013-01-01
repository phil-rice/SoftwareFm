package org.softwarefm.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.softwarefm.httpServer.IHttpServer;
import org.softwarefm.server.configurator.FriendsConfigurator;
import org.softwarefm.server.configurator.UsageConfigurator;
import org.softwarefm.server.friend.internal.FriendManagerServer;
import org.softwarefm.server.usage.internal.UsageMysqlCallbackAndGetter;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.shared.usage.UsageConstants;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.time.ITime;

public class SoftwareFmServer {

	private final IHttpServer httpServer;

	public SoftwareFmServer(DataSource dataSource, int port) {
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.ALL);
		httpServer = IHttpServer.Utils.server(10, IHttpServer.Utils.simpleParams("SoftwareFmUsageAndFriendsServer/1.0"), port, ICallback.Utils.sysErrCallback());
		DataSource datasource = MysqlTestData.dataSource;
		UsageMysqlCallbackAndGetter callbackAndGetter = new UsageMysqlCallbackAndGetter(datasource, ITime.Utils.system());
		FriendManagerServer friendManager = new FriendManagerServer(datasource);
		IUsagePersistance persistance = IUsagePersistance.Utils.persistance();
		httpServer.configure(new UsageConfigurator(callbackAndGetter, callbackAndGetter, persistance));
		httpServer.configure(new FriendsConfigurator(friendManager, persistance));
	}

	public void start() {
		httpServer.start();
	}

	public void shutdown() {
		httpServer.shutdown();
	}

	public static void main(String[] args) throws InterruptedException {
		new SoftwareFmServer(MysqlTestData.dataSource, UsageConstants.port).start();
		while (true)
			Thread.sleep(100000);
	}
}
