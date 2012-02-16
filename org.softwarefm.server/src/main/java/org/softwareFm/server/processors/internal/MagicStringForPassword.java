/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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