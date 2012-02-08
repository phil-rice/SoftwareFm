package org.softwareFm.server;

import java.io.File;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IUser;
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.server.internal.CrowdSourcedServer;
import org.softwareFm.server.internal.ServerUser;
import org.softwareFm.server.internal.UserCryptoFn;
import org.softwareFm.server.processors.IProcessCall;

public interface ICrowdSourcedServer {

	void shutdown();

	abstract public static class Utils {

		public static ICrowdSourcedServer fullServer(IGitOperations gitOperations, BasicDataSource dataSource, IProcessCall... processCalls) {
			return CrowdSourcedServer.makeServer(gitOperations, dataSource, processCalls);
		}

		public static ICrowdSourcedServer testServerPort(IProcessCall processCall, ICallback<Throwable> errorHandler) {
			return server(CommonConstants.testPort, 2, processCall, errorHandler);
		}

		public static ICrowdSourcedServer server(int port, int threads, IProcessCall processCall, ICallback<Throwable> errorHandler) {
			return new CrowdSourcedServer(port, threads, processCall, errorHandler, IUsage.Utils.defaultUsage());
		}

		public static File makeSfmRoot() {
			File root = new File(System.getProperty("user.home"));
			File sfmRoot = new File(root, ".sfm_remote");
			return sfmRoot;
		}

		public static IFunction1<Map<String, Object>, String> cryptoFn(DataSource dataSource) {
			return new UserCryptoFn(dataSource);
		}

		public static IUser makeUserForServer(IGitOperations gitOperations, IUrlGenerator userUrlGenerator, IFunction1<String, String> userRepositoryDefn){
			return new ServerUser(gitOperations, userUrlGenerator, userRepositoryDefn);
		}
		public static IUser makeUserForServer(IGitOperations gitOperations, IFunction1<String, String> userRepositoryDefn){
			return new ServerUser(gitOperations, LoginConstants.userGenerator(), userRepositoryDefn);
		}
		
	}
}
