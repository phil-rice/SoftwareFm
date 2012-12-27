package org.softwarefm.server.usage.internal;

import javax.sql.DataSource;

import org.softwarefm.eclipse.usage.IUsageStats;
import org.softwarefm.server.MySqlStrings;
import org.softwarefm.server.usage.IUsageCallback;
import org.softwarefm.utilities.time.ITime;
import org.springframework.jdbc.core.JdbcTemplate;

public class UsageMysqlCallback implements IUsageCallback {

	private final JdbcTemplate template;
	private final ITime time;

	public UsageMysqlCallback(DataSource dataSource, ITime time) {
		this.time = time;
		template = new JdbcTemplate(dataSource);
	}

	@Override
	public void process(String ip, String user, IUsageStats usageStats) {
		for (String key: usageStats.keys())
			template.update(MySqlStrings.insertIntoUsage, ip, user,key, usageStats.get(key).count, time.getNow());
	}

}
