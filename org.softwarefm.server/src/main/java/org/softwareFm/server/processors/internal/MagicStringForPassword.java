package org.softwareFm.server.processors.internal;

import java.text.MessageFormat;
import java.util.concurrent.Callable;

import javax.sql.DataSource;

import org.softwareFm.common.constants.LoginMessages;
import org.softwareFm.common.processors.AbstractLoginDataAccessor;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.server.processors.IMagicStringForPassword;

public class MagicStringForPassword extends AbstractLoginDataAccessor implements IMagicStringForPassword{

	private final Callable<String> magicStringGenerator;

	public MagicStringForPassword(DataSource dataSource, Callable<String> magicStringGenerator) {
		super(dataSource);
		this.magicStringGenerator = magicStringGenerator;
	}

	@Override
	public String allowResetPassword(String emailAddress) {
		String magicString = Callables.call(magicStringGenerator);
		int count = template.update(setPasswordResetKeyForUserSql, magicString, emailAddress);
		if (count == 0)
			throw new RuntimeException(MessageFormat.format(LoginMessages.emailAddressNotFound, emailAddress));
		return magicString;
	}

}
