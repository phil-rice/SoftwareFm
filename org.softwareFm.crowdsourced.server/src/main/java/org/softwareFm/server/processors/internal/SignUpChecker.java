package org.softwareFm.server.processors.internal;

import java.text.MessageFormat;
import java.util.concurrent.Callable;

import javax.sql.DataSource;

import org.softwareFm.server.constants.LoginMessages;
import org.softwareFm.server.processors.AbstractLoginDataAccessor;
import org.softwareFm.server.processors.ISignUpChecker;
import org.softwareFm.server.processors.SignUpResult;
import org.softwareFm.utilities.runnable.Callables;

public class SignUpChecker extends AbstractLoginDataAccessor implements ISignUpChecker {

	private final Callable<String> keyGenerator;

	public SignUpChecker(DataSource dataSource, Callable<String> keyGenerator) {
		super(dataSource);
		this.keyGenerator = keyGenerator;
	}

	@Override
	public SignUpResult signUp(String email, String salt, String passwordHash, String softwareFmId) {
		int existingEmail = template.queryForInt(countUsersWithEmailSql, email);
		if (existingEmail != 0)
			return new SignUpResult(MessageFormat.format(LoginMessages.existingEmailAddress, email), null);
		int existingSoftwareFmId = template.queryForInt(countUsersWithSoftwareFmIdSql, email);
		if (existingSoftwareFmId != 0)
			return new SignUpResult(MessageFormat.format(LoginMessages.existingSoftwareFmId, softwareFmId), null);
		String key = Callables.call(keyGenerator);
		template.update(addNewUserSql, email, salt, passwordHash, key, softwareFmId);
		return new SignUpResult(null, key);
	}

}
