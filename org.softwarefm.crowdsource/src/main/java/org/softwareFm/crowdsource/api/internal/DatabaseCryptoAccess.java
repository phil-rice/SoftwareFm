/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;

import javax.sql.DataSource;

import org.softwareFm.crowdsource.api.IIdAndSaltGenerator;
import org.softwareFm.crowdsource.api.IUserCryptoAccess;
import org.softwareFm.crowdsource.utilities.constants.LoginMessages;
import org.softwareFm.crowdsource.utilities.processors.AbstractLoginDataAccessor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

public class DatabaseCryptoAccess implements IUserCryptoAccess {
	private final JdbcTemplate template;
	private final IIdAndSaltGenerator idAndSaltGenerator;

	public DatabaseCryptoAccess(DataSource dataSource, IIdAndSaltGenerator idAndSaltGenerator) {
		this.idAndSaltGenerator = idAndSaltGenerator;
		this.template = new JdbcTemplate(dataSource);
	}

	@Override
	public boolean changePassword(String email, String oldHash, String newHash) {
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

	@Override
	public String allowResetPassword(String emailAddress) {
		String existing = template.queryForObject(AbstractLoginDataAccessor.getPasswordResetKeyForUserSql, String.class, emailAddress);
		if (existing != null)
			return existing;
		String magicString = idAndSaltGenerator.makeMagicString();
		int count = template.update(AbstractLoginDataAccessor.setPasswordResetKeyForUserSql, magicString, emailAddress);
		if (count == 0)
			throw new RuntimeException(MessageFormat.format(LoginMessages.emailAddressNotFound, emailAddress));
		return magicString;
	}

	@Override
	public String getCryptoForUser(String softwareFmId) {

		if (softwareFmId == null)
			throw new NullPointerException();
		String crypto = template.query(AbstractLoginDataAccessor.selectCryptoForSoftwareFmIdsql, new ResultSetExtractor<String>() {
			@Override
			public String extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next())
					return rs.getString("crypto");
				else
					return null;
			}
		}, softwareFmId);
		if (crypto == null)
			throw new NullPointerException(MessageFormat.format(LoginMessages.cannotWorkOutCryptFor, softwareFmId));
		return crypto;
	}

	@Override
	public String emailToSoftwareFmId(String softwareFmId) {
		return template.query(AbstractLoginDataAccessor.selectUsersWithEmailSql, new Object[] { softwareFmId }, new ResultSetExtractor<String>() {
			@Override
			public String extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					String softwareFmId = rs.getString("softwarefmid");
					return softwareFmId;
				} else
					return null;
			}
		});
	}
}