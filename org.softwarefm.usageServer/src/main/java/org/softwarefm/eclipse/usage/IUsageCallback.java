package org.softwarefm.eclipse.usage;

import javax.sql.DataSource;

import org.softwarefm.eclipse.usage.internal.UsageMysqlCallback;
import org.softwarefm.utilities.time.ITime;

public interface IUsageCallback {
	void process(String ip, String user, IUsage usage) throws Exception;
	
	public static class Utils{
		public static IUsageCallback mySqlCallback(DataSource dataSource, ITime time){
			return new UsageMysqlCallback(dataSource, time);
		}
	}
}
