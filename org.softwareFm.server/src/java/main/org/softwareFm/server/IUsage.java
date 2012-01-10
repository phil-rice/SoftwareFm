package org.softwareFm.server;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.softwareFm.server.internal.Usage;

public interface IUsage {
	void start();

	void monitor(final String ip, final String url, long duration);

	public static class Utils {

		public static IUsage defaultUsage() {
			return new Usage(dataSource());
		}

		public static DataSource dataSource() {
			BasicDataSource basicDataSource = new BasicDataSource();
			basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
			basicDataSource.setUrl("jdbc:mysql://localhost/softwarefm");
			basicDataSource.setUsername("root");
			basicDataSource.setPassword("iwtbde");
			return basicDataSource;
		}
	}
}