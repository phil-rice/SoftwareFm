package org.softwareFm.server.processors.internal;

import java.text.MessageFormat;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.AbstractLoginDataAccessor;
import org.softwareFm.server.processors.IForgottonPasswordMailer;
import org.softwareFm.utilities.exceptions.WrappedException;

public class ForgottonPasswordMailer extends AbstractLoginDataAccessor implements IForgottonPasswordMailer {

	private final String host;
	private final String username;
	private final String password;

	public ForgottonPasswordMailer(DataSource dataSource, String host, String username, String password) {
		super(dataSource);
		this.host = host;
		this.username = username;
		this.password = password;
	}

	@Override
	public String process(String emailAddress) {
		try {
			String magicString = UUID.randomUUID().toString();
			int count = template.update("update users set passwordResetKey=? where email=?", magicString, emailAddress);
			if (count == 0)
				throw new RuntimeException(MessageFormat.format(ServerConstants.emailAddressNotFound, emailAddress));

			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(25);
			if (username != null && password != null)
				email.setAuthentication(username, password);
			email.setDebug(true);
			email.setTLS(false);
			email.setFrom("forgottonpasswords@softwarefm.org");
			email.setSubject(ServerConstants.passwordResetSubject);
			String message = MessageFormat.format(ServerConstants.forgottonPasswordMessage, emailAddress, magicString);
			email.setMsg(message);
			email.addTo(emailAddress);
			if (host != null)
				email.send();
			return magicString;
		} catch (EmailException e) {
			throw WrappedException.wrap(e);
		}
	}

	public static void main(String[] args) {
		try {
			new SignUpChecker(SignUpChecker.defaultDataSource()).signUp("phil.rice.erudine@googlemail.com", "someSalt", "somepassword");
		} catch (Exception e) {
		}
		new ForgottonPasswordMailer(ForgottonPasswordMailer.defaultDataSource(),"smtp.gmail.com", "phil.rice.erudine@googlemail.com", "elrx4321").process("phil.rice.erudine@googlemail.com");

	}

}
