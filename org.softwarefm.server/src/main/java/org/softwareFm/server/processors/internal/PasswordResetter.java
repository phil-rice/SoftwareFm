package org.softwareFm.server.processors.internal;

import java.util.UUID;

import javax.sql.DataSource;

import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.processors.AbstractLoginDataAccessor;
import org.softwareFm.server.processors.IPasswordResetter;
import org.springframework.dao.DataAccessException;

public class PasswordResetter extends AbstractLoginDataAccessor implements IPasswordResetter {

	public PasswordResetter(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public String reset(String magicString) {
		String newPassword = UUID.randomUUID().toString();
		try {
			String salt = template.queryForObject(selectSaltFromUserWithPasswordResetKeySql, String.class, magicString);
			String newDigest = Crypto.digest(salt, newPassword);
			template.update(updatePasswordAndClearResetKeySql, newDigest, magicString);
			return newPassword;
		} catch (DataAccessException e) {
			return null;
		}
	}

}
