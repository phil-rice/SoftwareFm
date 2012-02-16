/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.processors.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Map;

import javax.sql.DataSource;

import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.constants.LoginMessages;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.processors.AbstractLoginDataAccessor;
import org.softwareFm.server.processors.ILoginChecker;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class LoginChecker extends AbstractLoginDataAccessor implements ILoginChecker {

	public LoginChecker(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public Map<String, String> login(final String email, final String passwordHash) {
		Map<String, String> result = template.query(selectUsersWithEmailAndPasswordHashSql, new Object[] { email, passwordHash }, new ResultSetExtractor<Map<String,String>>() {
			@Override
			public Map<String,String> extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					String crypto = rs.getString("crypto");
					String softwareFmId = rs.getString("softwarefmid");
					if (rs.next())
						throw new IllegalStateException(MessageFormat.format(LoginMessages.duplicateEmailAndPassword, email, passwordHash));
					return Maps.<String, String> makeMap(LoginConstants.softwareFmIdKey, softwareFmId, LoginConstants.cryptoKey, crypto);
				} else
					return null;
			}
		});
		return result;
	}

}