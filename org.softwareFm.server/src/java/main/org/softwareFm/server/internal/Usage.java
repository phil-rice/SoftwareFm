package org.softwareFm.server.internal;


import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class Usage {

	public static DataSource makeLocalDataSource() {
		BasicDataSource basicDataSource = new BasicDataSource();
		basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
		basicDataSource.setUrl("jdbc:mysql://localhost/softwarefm");
		basicDataSource.setUsername("root");
		basicDataSource.setPassword("iwtbde");
		return basicDataSource;
	}

	private final JdbcTemplate template;

	public Usage(DataSource dataSource) {
		template = new JdbcTemplate(dataSource);
	}

	public void start() {
		try {
			template.queryForInt("select count(*) from `usage`");
		} catch (DataAccessException e) {
			template.execute("create table `usage` (ip varchar(100), url varchar(200), stamp varchar(100))");
		}
	}

	public void monitor(final String ip, final String url) {
//		template.execute("insert into `usage`(ip, url, stamp) values('"+ ip+"','" + url+"','" + new Date().toString()+"')");
		template.update("insert into `usage`(ip, url, stamp) values(?,?,?)",ip, url, new Date().toString());
	}

	public static void main(String[] args) {
		Usage usage = new Usage(makeLocalDataSource());
		usage.start();
		usage.monitor("ip1","url1");
		usage.monitor("ip2","url2");
	}

}
