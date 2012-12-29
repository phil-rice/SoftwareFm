package org.softwarefm.server.usage.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.softwarefm.server.MySqlStrings;
import org.softwarefm.server.usage.IUsageCallbackAndGetter;
import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.utilities.maps.Maps;
import org.softwarefm.utilities.time.ITime;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

public class UsageMysqlCallbackAndGetter implements IUsageCallbackAndGetter {

	private final JdbcTemplate template;
	private final ITime time;

	public UsageMysqlCallbackAndGetter(DataSource dataSource, ITime time) {
		this.time = time;
		template = new JdbcTemplate(dataSource);
	}

	@Override
	public void process(String ip, String user, IUsageStats usageStats) {
		for (String key: usageStats.keys())
			template.update(MySqlStrings.insertIntoUsage, ip, user,key, usageStats.get(key).count, time.getNow());
	}

	@Override
	public IUsageStats getStats(String user) {
		final Map<String, Integer> result = new HashMap<String, Integer>();
		template.query(MySqlStrings.selectFromUsage, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Maps.add(result, rs.getString(MySqlStrings.pathField), rs.getInt(MySqlStrings.timesField));
			}
		}, user);
		return IUsageStats.Utils.fromIntegerMap(result);
	}

}
