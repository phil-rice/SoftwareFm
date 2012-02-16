/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IUser;
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.processors.AbstractLoginDataAccessor;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.server.internal.CrowdSourcedServer;
import org.softwareFm.server.internal.ServerUser;
import org.softwareFm.server.internal.UserCryptoFn;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.ProcessCallParameters;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

public interface ICrowdSourcedServer {

	void shutdown();

	abstract public static class Utils {

		public static ICrowdSourcedServer fullServer(int port, IGitOperations gitOperations, BasicDataSource dataSource, IFunction1<ProcessCallParameters, IProcessCall[]> extraProcessCalls, String prefix) {
			return CrowdSourcedServer.makeServer(port, gitOperations, dataSource, extraProcessCalls, prefix);
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

		public static IFunction1<String, String> emailToSoftwareFmId(final DataSource dataSource) {
			return new IFunction1<String, String>() {
				private final JdbcTemplate template = new JdbcTemplate(dataSource);

				@Override
				public String apply(String from) throws Exception {
					return template.query(AbstractLoginDataAccessor.selectUsersWithEmailSql, new Object[] { from }, new ResultSetExtractor<String>() {
						@Override
						public String extractData(ResultSet rs) throws SQLException, DataAccessException {
							if (rs.next()) {
								String softwareFmId = rs.getString("softwarefmid");
								return softwareFmId;
							} else
								return null;
						}
					});
				}
			};
		}

		public static IFunction1<Map<String, Object>, String> cryptoFn(DataSource dataSource) {
			return new UserCryptoFn(dataSource);
		}

		public static IUser makeUserForServer(IGitOperations gitOperations, IUrlGenerator userUrlGenerator, IFunction1<String, String> userRepositoryDefn, Map<String, Callable<Object>> defaultValues) {
			return new ServerUser(gitOperations, userUrlGenerator, userRepositoryDefn, defaultValues);
		}

		public static IUser makeUserForServer(IGitOperations gitOperations, IFunction1<String, String> userRepositoryDefn, Map<String, Callable<Object>> defaultValues, String prefix) {
			return new ServerUser(gitOperations, LoginConstants.userGenerator(prefix), userRepositoryDefn, defaultValues);
		}

		public static int port(String[] args) {
			if (args.length > 0)
				return Integer.parseInt(args[0]);
			else
				return CommonConstants.serverPort;
		}

	}
}