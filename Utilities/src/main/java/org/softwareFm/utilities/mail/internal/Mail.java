package org.softwareFm.utilities.mail.internal;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.mail.IMail;

public class Mail implements IMail {

	private final String username;
	private final String password;
	private final String hostName;
	private final String from;

	public Mail(String hostName, String username, String password, String from) {
		this.username = username;
		this.password = password;
		this.hostName = hostName;
		this.from = from;
	}

	@Override
	public void send(String to, String subject, String message) {
		try {
			Email email = new SimpleEmail();
			email.setSmtpPort(587);
			email.setAuthentication(username, password);
			email.setHostName(hostName);
			email.setTLS(true);
			email.setDebug(true);
			email.setFrom(from);
			email.setSubject(subject);
			email.setMsg(message);
			email.addTo(to);
			email.send();
		} catch (EmailException e) {
			throw WrappedException.wrap(e);
		}
	}

}
