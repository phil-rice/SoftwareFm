package org.softwarefm.eclipse.usage.internal;

import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.softwarefm.eclipse.usage.IUsage;
import org.softwarefm.eclipse.usage.IUsageCallback;
import org.softwarefm.eclipse.usage.UsageStats;
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
		Map<String, UsageStats> stats = usage.getStats();
		for (Entry<String, UsageStats> entry: stats.entrySet())
			template.update("insert into usage_table (ip, user,path,times, time) values(?,?,?,?,?)", ip, user, entry.getKey(), entry.getValue().count, time.getNow());
	}

}
