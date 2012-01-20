package org.softwareFm.server.processors.internal;

import java.util.UUID;

import javax.sql.DataSource;

import org.softwareFm.server.processors.AbstractLoginDataAccessor;
import org.softwareFm.server.processors.IPasswordResetter;
import org.softwareFm.utilities.crypto.Crypto;
import org.springframework.dao.DataAccessException;

public class PasswordResetter extends AbstractLoginDataAccessor implements IPasswordResetter {

	public PasswordResetter(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public String reset(String magicString) {
		String newPassword = UUID.randomUUID().toString();
		try {
			String salt = template.queryForObject("select salt from users where passwordResetKey=?", String.class, magicString);
			String newDigest = Crypto.digest(salt, newPassword);
			template.update("update users set passwordResetKey=null,password=? where passwordResetKey=?", newDigest, magicString);
			return newPassword;
		} catch (DataAccessException e) {
			return null;
		}
	}

}
