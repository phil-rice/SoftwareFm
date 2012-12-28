package org.softwarefm.server.usage;

import javax.sql.DataSource;

import org.softwarefm.server.usage.internal.UsageMysqlCallbackAndGetter;
import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.utilities.time.ITime;

public interface IUsageCallback {

	void process(String ip, String user, IUsageStats usageStats);

	

	public static class Utils {
		public static IUsageCallback mySqlCallback(DataSource dataSource, ITime time) {
			return new UsageMysqlCallbackAndGetter(dataSource, time);
		}
	}
}
