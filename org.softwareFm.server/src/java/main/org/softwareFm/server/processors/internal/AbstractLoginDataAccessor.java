package org.softwareFm.server.processors.internal;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

abstract public class AbstractLoginDataAccessor {

	private final static DataSource dataSource = makeDataSource();
	protected final JdbcTemplate template;

	public AbstractLoginDataAccessor() {
		this.template = new JdbcTemplate(dataSource);
	}

	private static DataSource makeDataSource() {
		BasicDataSource basicDataSource = new BasicDataSource();
		basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
		basicDataSource.setUrl("jdbc:mysql://localhost/users");
		basicDataSource.setUsername("root");
		basicDataSource.setPassword("iwtbde");
		JdbcTemplate template = new JdbcTemplate(basicDataSource);
		try {
			template.update("create table users (email varchar(200), salt varchar(100), password text, crypto varchar(200), passwordResetKey varchar(200))");
		} catch (Exception e) {
		}
		return basicDataSource;
	}
}
