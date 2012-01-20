package org.softwareFm.server.processors.internal;

import java.text.MessageFormat;

import javax.sql.DataSource;

import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.AbstractLoginDataAccessor;
import org.softwareFm.server.processors.ISignUpChecker;
import org.softwareFm.server.processors.SignUpResult;
import org.softwareFm.utilities.crypto.Crypto;

public class SignUpChecker extends AbstractLoginDataAccessor implements ISignUpChecker {

	public SignUpChecker(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public SignUpResult signUp(String email, String salt, String passwordHash) {
		int existing = template.queryForInt("select count(*) from users where email = ?", email);
		if (existing == 0) {
			String key = Crypto.makeKey();
			template.update("insert into users (email, salt, password, crypto) values(?,?,?,?)", email, salt, passwordHash, key);
			return new SignUpResult(null, key);
		}
		return new SignUpResult(MessageFormat.format(ServerConstants.existingEmailAddress, email), null);
	}

}
