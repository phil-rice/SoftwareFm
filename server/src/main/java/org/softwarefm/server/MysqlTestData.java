package org.softwarefm.server;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class MysqlTestData {
	public final static BasicDataSource dataSource = new BasicDataSource();
	static {
		dataSource.setUrl("jdbc:mysql://localhost:3306/softwarefm");
		dataSource.setUsername("root");
		dataSource.setPassword("iwtbde");
		
	}

	public final static JdbcTemplate template = new JdbcTemplate(dataSource);

}
