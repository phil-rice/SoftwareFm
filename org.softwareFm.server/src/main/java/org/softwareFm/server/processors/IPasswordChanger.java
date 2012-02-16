/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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