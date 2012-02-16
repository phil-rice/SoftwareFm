/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.processors;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

abstract public class AbstractLoginDataAccessor {
	public final static String createUsersSql = "create table users (email varchar(200), softwarefmid varchar(100), salt varchar(100), password text, crypto varchar(200), passwordResetKey varchar(200))";
	public final static String selectUsersWithEmailSql = "select * from users where email = ?";
	public final static String setPasswordResetKeyForUserSql = "update users set passwordResetKey=? where email=?";
	public final static String selectUsersWithEmailAndPasswordHashSql = "select * from users where email = ? and password=?";
	public final static String selectSaltFromUserWithPasswordResetKeySql = "select salt from users where passwordResetKey=?";
	public final static String updatePasswordAndClearResetKeySql = "update users set passwordResetKey=null,password=? where passwordResetKey=?";
	public final static String countUsersWithEmailSql = "select count(*) from users where email = ?";
	public final static String countUsersWithSoftwareFmIdSql = "select count(*) from users where softwarefmid = ?";
	public final static String addNewUserSql = "insert into users (email, salt, password, crypto, softwarefmid) values(?,?,?,?,?)";
	public final static String selectCryptoForSoftwareFmIdsql = "select crypto from users where softwarefmid=?";

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