package org.softwareFm.server.processors.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Map;

import javax.sql.DataSource;

import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.AbstractLoginDataAccessor;
import org.softwareFm.server.processors.ILoginChecker;
import org.softwareFm.utilities.maps.Maps;
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
						throw new IllegalStateException(MessageFormat.format(ServerConstants.duplicateEmailAndPassword, email, passwordHash));
					return Maps.<String, String> makeMap(ServerConstants.softwareFmIdKey, softwareFmId, ServerConstants.cryptoKey, crypto);
				} else
					return null;
			}
		});
		return result;
	}

}
