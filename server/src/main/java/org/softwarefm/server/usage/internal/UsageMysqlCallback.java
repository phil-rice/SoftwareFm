package org.softwarefm.server.usage.internal;

import javax.sql.DataSource;

import org.softwarefm.eclipse.usage.IUsage;
import org.softwarefm.eclipse.usage.UsageStatData;
import org.softwarefm.server.MySqlStrings;
import org.softwarefm.server.usage.IUsageCallback;
import org.softwarefm.utilities.maps.ISimpleMap;
import org.softwarefm.utilities.time.ITime;
import org.springframework.jdbc.core.JdbcTemplate;

public class UsageMysqlCallback implements IUsageCallback {

	private final JdbcTemplate template;
	private final ITime time;

	public UsageMysqlCallback(DataSource dataSource, ITime time) {
		this.time = time;
		template = new JdbcTemplate(dataSource);
	}

	public void process(String ip, String user, IUsage usage) throws Exception {
		ISimpleMap<String, UsageStatData> stats = usage.getStats();
		for (String key: stats.keys())
			template.update(MySqlStrings.insertIntoUsage, ip, user,key, stats.get(key).count, time.getNow());
	}

}
