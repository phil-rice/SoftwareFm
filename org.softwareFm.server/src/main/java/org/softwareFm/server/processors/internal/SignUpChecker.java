/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.processors.internal;

import java.text.MessageFormat;
import java.util.concurrent.Callable;

import javax.sql.DataSource;

import org.softwareFm.common.IUser;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.constants.LoginMessages;
import org.softwareFm.common.processors.AbstractLoginDataAccessor;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.server.processors.ISignUpChecker;
import org.softwareFm.server.processors.SignUpResult;

public class SignUpChecker extends AbstractLoginDataAccessor implements ISignUpChecker {

	private final Callable<String> keyGenerator;
	private final IUser user;

	public SignUpChecker(DataSource dataSource, Callable<String> keyGenerator, IUser user) {
		super(dataSource);
		this.keyGenerator = keyGenerator;
		this.user = user;
	}

	@Override
	public SignUpResult signUp(String email, String moniker, String salt, String passwordHash, String softwareFmId) {
		int existingEmail = template.queryForInt(countUsersWithEmailSql, email);
		if (existingEmail != 0)
			return new SignUpResult(MessageFormat.format(LoginMessages.existingEmailAddress, email), null);
		int existingSoftwareFmId = template.queryForInt(countUsersWithSoftwareFmIdSql, email);
		if (existingSoftwareFmId != 0)
			return new SignUpResult(MessageFormat.format(LoginMessages.existingSoftwareFmId, softwareFmId), null);
		String key = Callables.call(keyGenerator);
		template.update(addNewUserSql, email, salt, passwordHash, key, softwareFmId);
		user.setUserProperty(softwareFmId, key, LoginConstants.emailKey, email);
		user.setUserProperty(softwareFmId, key, LoginConstants.monikerKey, moniker);
		return new SignUpResult(null, key);
	}

}