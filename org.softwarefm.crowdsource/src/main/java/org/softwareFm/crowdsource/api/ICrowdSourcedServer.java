/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.sql.DataSource;

import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.api.user.IUser;
import org.softwareFm.crowdsource.user.internal.ServerUser;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.processors.AbstractLoginDataAccessor;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

public interface ICrowdSourcedServer {

	void shutdown();

	abstract public static class Utils {


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