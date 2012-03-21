package org.softwareFm.crowdsource.api.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Map;

import javax.sql.DataSource;

import org.softwareFm.crowdsource.api.IIdAndSaltGenerator;
import org.softwareFm.crowdsource.api.IUserCryptoAccess;
import org.softwareFm.crowdsource.server.internal.UserCryptoFn;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginMessages;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.processors.AbstractLoginDataAccessor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

public class DatabaseCryptoAccess implements IUserCryptoAccess {
	private final JdbcTemplate template;
	private final UserCryptoFn userCryptoFn;
	private final IIdAndSaltGenerator idAndSaltGenerator;

	public DatabaseCryptoAccess(DataSource dataSource, IIdAndSaltGenerator idAndSaltGenerator) {
		this.idAndSaltGenerator = idAndSaltGenerator;
		this.template = new JdbcTemplate(dataSource);
		this.userCryptoFn = new UserCryptoFn(dataSource);
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
	public IFunction1<Map<String, Object>, String> userCryptoFn() {
		return userCryptoFn;
	}

	@Override
	public String getCryptoForUser(String softwareFmId) {
		return Functions.call(userCryptoFn, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
	}

	@Override
	public String emailToSoftwareFnId(String softwareFmId) {
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