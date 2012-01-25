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
	public SignUpResult signUp(String email, String salt, String passwordHash, String softwareFmId) {
		int existingEmail = template.queryForInt(countUsersWithEmailSql, email);
		if (existingEmail != 0)
			return new SignUpResult(MessageFormat.format(ServerConstants.existingEmailAddress, email), null);
		int existingSoftwareFmId = template.queryForInt(countUsersWithSoftwareFmIdSql, email);
		if (existingSoftwareFmId != 0)
			return new SignUpResult(MessageFormat.format(ServerConstants.existingSoftwareFmId, softwareFmId), null);
		String key = Crypto.makeKey();
		template.update(addNewUserSql, email, salt, passwordHash, key, softwareFmId);
		return new SignUpResult(null, key);
	}

}
