package org.softwareFm.server.internal;

import java.util.Date;

import javax.sql.DataSource;

import org.softwareFm.server.IUsage;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class Usage implements IUsage {

	private final JdbcTemplate template;

	public Usage(DataSource dataSource) {
		template = new JdbcTemplate(dataSource);
	}

	@Override
	public void start() {
		try {
			template.queryForInt("select count(*) from `usage`");
		} catch (DataAccessException e) {
			template.execute("create table `usage` (ip varchar(100), url varchar(200), stamp varchar(100), long duration)");
		}
	}

	@Override
	public void monitor(final String ip, final String url, long duration) {
		// template.execute("insert into `usage`(ip, url, stamp) values('"+ ip+"','" + url+"','" + new Date().toString()+"')");
		template.update("insert into `usage`(ip, url, stamp, duration) values(?,?,?,?)", ip, url, new Date().toString(), duration);
	}

	public static void main(String[] args) {
		IUsage usage = IUsage.Utils.defaultUsage();
		usage.start();
		usage.monitor("ip1", "url1", 10);
		usage.monitor("ip2", "url2", 20);
	}

}
