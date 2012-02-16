/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.processors;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.softwareFm.common.exceptions.WrappedException;

public interface IMailer {
	void mail(String from, String to, String subject, String message);

	abstract public static class Utils {
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