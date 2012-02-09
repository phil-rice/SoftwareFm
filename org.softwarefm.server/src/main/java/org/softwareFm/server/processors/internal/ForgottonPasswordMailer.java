package org.softwareFm.server.processors.internal;

import java.text.MessageFormat;

import org.softwareFm.common.constants.LoginMessages;
import org.softwareFm.server.processors.IForgottonPasswordMailer;
import org.softwareFm.server.processors.IMagicStringForPassword;
import org.softwareFm.server.processors.IMailer;

public class ForgottonPasswordMailer implements IForgottonPasswordMailer {

	private final IMailer mailer;
	private final IMagicStringForPassword magicStringForPassword;

	public ForgottonPasswordMailer( IMailer mailer, IMagicStringForPassword magicStringForPassword) {
		this.mailer = mailer;
		this.magicStringForPassword = magicStringForPassword;
	}

	@Override
	public String process(String emailAddress) {
		String magicString = magicStringForPassword.allowResetPassword(emailAddress);

		String message = MessageFormat.format(LoginMessages.forgottonPasswordMessage, emailAddress, magicString);
		mailer.mail("forgottonpasswords@softwarefm.org", emailAddress, LoginMessages.passwordResetSubject, message);
		return magicString;
	}


}
