/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Map;

import javax.sql.DataSource;

import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginMessages;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.processors.AbstractLoginDataAccessor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class UserCryptoFn extends AbstractLoginDataAccessor implements IFunction1<Map<String, Object>, String> {

	public UserCryptoFn(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public String apply(Map<String, Object> from) throws Exception {
		String softwareFmId = (String) from.get(LoginConstants.softwareFmIdKey);
		if (softwareFmId == null)
			throw new NullPointerException(from.toString());
		String crypto = template.query(selectCryptoForSoftwareFmIdsql, new ResultSetExtractor<String>() {
			@Override
			public String extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next())
					return rs.getString("crypto");
				else
					return null;
			}
		},softwareFmId);
		if (crypto == null)
			throw new NullPointerException(MessageFormat.format(LoginMessages.cannotWorkOutCryptFor, softwareFmId, from));
		return crypto;
	}

}