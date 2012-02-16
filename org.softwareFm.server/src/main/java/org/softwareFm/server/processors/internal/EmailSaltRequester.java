/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.processors.internal;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.softwareFm.common.processors.AbstractLoginDataAccessor;
import org.softwareFm.server.processors.IEmailSaltRequester;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class EmailSaltRequester extends AbstractLoginDataAccessor implements IEmailSaltRequester {

	public EmailSaltRequester(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public String getSalt(String email) {
		return template.query(selectUsersWithEmailSql, new Object[] { email }, new ResultSetExtractor<String>() {
			@Override
			public String extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					String salt = rs.getString("salt");
					if (!rs.next())
						return salt;
				} else
					return null;
				throw new RuntimeException();
			}
		});
	}

}