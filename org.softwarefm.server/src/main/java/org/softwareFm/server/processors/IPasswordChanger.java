package org.softwareFm.server.processors;

import javax.sql.DataSource;

import org.softwareFm.server.processors.internal.PasswordChangerMock;
import org.springframework.jdbc.core.JdbcTemplate;

public interface IPasswordChanger {

	/** returns false if failed */
	boolean changePassword(String email, String oldHash, String newHash);

	abstract public static class Utils {
		public static PasswordChangerMock mockPasswordChanger() {
			return new PasswordChangerMock();
		}

		public static IPasswordChanger databasePasswordChanger(final DataSource dataSource) {
			return new IPasswordChanger() {

				@Override
				public boolean changePassword(String email, String oldHash, String newHash) {
					JdbcTemplate template = new JdbcTemplate(dataSource);
					int count = template.queryForInt("select count(*) from users where email = ? and password = ?", email, oldHash);
					switch (count) {
					case 0:
						return false;
					case 1:
						template.update("update users set password =? where email =? and password =?", newHash, email, oldHash);
						return true;
					default:
						throw new IllegalStateException(email);
					}
				}

			};
		}
	}
}
