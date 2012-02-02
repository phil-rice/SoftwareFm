package org.softwareFm.server.processors.internal;

import java.text.MessageFormat;
import java.util.UUID;

import javax.sql.DataSource;

import org.softwareFm.common.constants.LoginMessages;
import org.softwareFm.common.processors.AbstractLoginDataAccessor;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.server.processors.IForgottonPasswordMailer;
import org.softwareFm.server.processors.IMailer;

public class ForgottonPasswordMailer extends AbstractLoginDataAccessor implements IForgottonPasswordMailer {

	private final IMailer mailer;

	public ForgottonPasswordMailer(DataSource dataSource, IMailer mailer) {
		super(dataSource);
		this.mailer = mailer;
	}

	@Override
	public String process(String emailAddress) {
		String magicString = UUID.randomUUID().toString();
		int count = template.update(setPasswordResetKeyForUserSql, magicString, emailAddress);
		if (count == 0)
			throw new RuntimeException(MessageFormat.format(LoginMessages.emailAddressNotFound, emailAddress));

		String message = MessageFormat.format(LoginMessages.forgottonPasswordMessage, emailAddress, magicString);
		mailer.mail("forgottonpasswords@softwarefm.org", emailAddress, LoginMessages.passwordResetSubject, message);
		return magicString;
	}

	public static void main(String[] args) {
		try {
			new SignUpChecker(SignUpChecker.defaultDataSource(), Callables.<String>exceptionIfCalled()).signUp("phil.rice.erudine@googlemail.com", "someSalt", "somepassword", "someSoftwareFmId");
		} catch (Exception e) {
		}
		new ForgottonPasswordMailer(ForgottonPasswordMailer.defaultDataSource(), IMailer.Utils.email("smtp.gmail.com", "your name", "your password")).process("phil.rice.erudine@googlemail.com");

	}

}
