package org.softwareFm.server.processors;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.softwareFm.utilities.exceptions.WrappedException;

public interface IMailer {
	void mail(String from, String to, String subject, String message);

	public static class Utils {
		public static IMailer email(final String hostName, final String userName, final String password) {
			return new IMailer() {
				@Override
				public void mail(String from, String to, String subject, String message) {
					try {
						Email email = new SimpleEmail();
						email.setHostName(hostName);
						email.setSmtpPort(25);
						if (userName != null && password != null)
							email.setAuthentication(userName, password);
						email.setDebug(true);
						email.setTLS(false);
						email.setFrom(from);
						email.setSubject(subject);
						email.setMsg(message);
						email.addTo(to);
						email.send();
					} catch (EmailException e) {
						throw WrappedException.wrap(e);
					}
				}
			};
		}

		public static IMailer noMailer() {
			return new IMailer() {
				@Override
				public void mail(String from, String to, String subject, String message) {
				}
			};
		}
	}
}
