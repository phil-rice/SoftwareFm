package org.softwareFm.utilities.mail;

import org.softwareFm.utilities.mail.internal.Mail;

public interface IMail {
	void send(String to, String subject, String message);

	public static class Utils {
		public static IMail mail(String hostName, String username, String password, String from) {
			return new Mail(hostName, username, password, from);
		}
	}
}
