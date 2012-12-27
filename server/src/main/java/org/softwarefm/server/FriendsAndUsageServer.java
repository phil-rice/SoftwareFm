package org.softwarefm.server;



import javax.sql.DataSource;

import org.apache.http.protocol.HttpRequestHandler;
import org.softwarefm.eclipse.usage.IUsagePersistance;
import org.softwarefm.server.friend.internal.FriendManagerServer;
import org.softwarefm.server.friend.internal.FriendServerHandler;
import org.softwarefm.server.usage.internal.UsageMysqlCallbackAndGetter;
import org.softwarefm.server.usage.internal.UsageServerHandler;
import org.softwarefm.shared.friend.IFriendAndFriendManager;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.http.IHttpServer;
import org.softwarefm.utilities.time.ITime;

public class FriendsAndUsageServer implements IHttpServer{

	private final IHttpServer server;
	 IFriendAndFriendManager friendManager;

	public FriendsAndUsageServer(int port, DataSource dataSource, ICallback<Throwable> errorHandler) {
		server = IHttpServer.Utils.server(10, IHttpServer.Utils.simpleParams("Friends and Usage Server/1.0"), port, errorHandler);
		IUsagePersistance persistance = IUsagePersistance.Utils.persistance();
		UsageMysqlCallbackAndGetter usageMysqlCallback = new UsageMysqlCallbackAndGetter(dataSource, ITime.Utils.system());
		server.register("/usage/*", new UsageServerHandler(persistance, usageMysqlCallback));
		server.register("/user/*", new FriendServerHandler(persistance, friendManager =new FriendManagerServer(dataSource)));
		server.start();
	}

	public void start() {
		server.start();
	}

	public void shutdown() {
		server.shutdown();
	}

	public void register(String path, HttpRequestHandler handler) {
		server.register(path, handler);
	}

	public static void main(String[] args) throws InterruptedException {
		new FriendsAndUsageServer(80, MysqlTestData.dataSource, ICallback.Utils.sysErrCallback());
		while (true) {
			Thread.sleep(1000000);
		}
	}

}
