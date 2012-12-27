package org.softwarefm.server.usage;

import javax.sql.DataSource;

import org.softwarefm.eclipse.usage.IUsageStats;
import org.softwarefm.server.usage.internal.UsageMysqlCallback;
import org.softwarefm.utilities.time.ITime;

public interface IUsageCallback {
	
	void process(String ip, String user, IUsageStats usageStats) ;
	
	public static class Utils{
		public static IUsageCallback mySqlCallback(DataSource dataSource, ITime time){
			return new UsageMysqlCallback(dataSource, time);
		}
	}
}
