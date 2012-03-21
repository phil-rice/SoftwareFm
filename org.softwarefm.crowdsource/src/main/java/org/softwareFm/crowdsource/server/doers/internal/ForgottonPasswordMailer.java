/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.doers.internal;

import java.text.MessageFormat;

import org.softwareFm.crowdsource.api.server.IForgottonPasswordMailer;
import org.softwareFm.crowdsource.api.server.IMagicStringForPassword;
import org.softwareFm.crowdsource.api.server.IMailer;
import org.softwareFm.crowdsource.utilities.constants.LoginMessages;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;

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
		try {
			mailer.mail("forgottonpasswords@softwarefm.org", emailAddress, LoginMessages.passwordResetSubject, message);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(message);
			throw WrappedException.wrap(e);
		}
		return magicString;
	}


}