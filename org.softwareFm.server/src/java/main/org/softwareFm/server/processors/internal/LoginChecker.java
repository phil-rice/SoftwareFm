package org.softwareFm.server.processors.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;

import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.ILoginChecker;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class LoginChecker extends AbstractLoginDataAccessor implements ILoginChecker {

	@Override
	public String login(final String email, final String passwordHash) {
		String crypto = template.query("select * from users where email = ? and password=?", new Object[] { email, passwordHash }, new ResultSetExtractor<String>() {
			@Override
			public String extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					String crypto = rs.getString("crypto");
					if (rs.next())
						throw new IllegalStateException(MessageFormat.format(ServerConstants.duplicateEmailAndPassword, email, passwordHash));
					return crypto;
				} else
					return null;
			}
		});
		return crypto;
	}

}
