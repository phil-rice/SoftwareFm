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
