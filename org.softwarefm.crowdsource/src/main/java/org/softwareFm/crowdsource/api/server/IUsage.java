/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.server;

import org.apache.commons.dbcp.BasicDataSource;
import org.softwareFm.crowdsource.server.internal.Usage;

public interface IUsage {
	void start();

	void monitor(final String ip, final String url, long duration);

	void shutdown();

	abstract public static class Utils {

		public static IUsage defaultUsage() {
			return new Usage(dataSource());
		}

		public static IUsage noUsage() {
			return new IUsage() {
				@Override
				public void start() {
				}

				@Override
				public void shutdown() {
				}

				@Override
				public void monitor(String ip, String url, long duration) {
				}
			};
		}

		public static BasicDataSource dataSource() {
			BasicDataSource basicDataSource = new BasicDataSource();
			basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
			basicDataSource.setUrl("jdbc:mysql://localhost/softwarefm");
			basicDataSource.setUsername("root");
			basicDataSource.setPassword("iwtbde");
			return basicDataSource;
		}
	}

}