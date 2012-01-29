package org.softwareFm.server.processors;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

abstract public class AbstractLoginDataAccessor {
	protected final static String createUsersSql = "create table users (email varchar(200), softwarefmid varchar(100), salt varchar(100), password text, crypto varchar(200), passwordResetKey varchar(200))";
	protected final static String selectUsersWithEmailSql = "select * from users where email = ?";
	protected final static String setPasswordResetKeyForUserSql = "update users set passwordResetKey=? where email=?";
	protected final static String selectUsersWithEmailAndPasswordHashSql = "select * from users where email = ? and password=?";
	protected final static String selectSaltFromUserWithPasswordResetKeySql = "select salt from users where passwordResetKey=?";
	protected final static String updatePasswordAndClearResetKeySql = "update users set passwordResetKey=null,password=? where passwordResetKey=?";
	protected final static String countUsersWithEmailSql = "select count(*) from users where email = ?";
	protected final static String countUsersWithSoftwareFmIdSql = "select count(*) from users where softwarefmid = ?";
	protected final static String addNewUserSql = "insert into users (email, salt, password, crypto, softwarefmid) values(?,?,?,?,?)";
	protected final static String selectCryptoForSoftwareFmIdsql = "select crypto from users where softwarefmid=?";

	protected final JdbcTemplate template;

	public AbstractLoginDataAccessor(DataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
	}

	public static BasicDataSource defaultDataSource() {
		BasicDataSource basicDataSource = new BasicDataSource();
		basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
		basicDataSource.setUrl("jdbc:mysql://localhost/users");
		basicDataSource.setUsername("root");
		basicDataSource.setPassword("iwtbde");
		JdbcTemplate template = new JdbcTemplate(basicDataSource);
		try {
			template.update(createUsersSql);
		} catch (Exception e) {
		}
		return basicDataSource;
	}
}
