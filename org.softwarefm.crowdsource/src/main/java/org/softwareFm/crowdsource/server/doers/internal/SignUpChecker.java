/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.doers.internal;

import java.text.MessageFormat;

import javax.sql.DataSource;

import org.softwareFm.crowdsource.api.ICrowdSourcedReadWriteApi;
import org.softwareFm.crowdsource.api.ICryptoGenerators;
import org.softwareFm.crowdsource.api.server.ISignUpChecker;
import org.softwareFm.crowdsource.api.server.SignUpResult;
import org.softwareFm.crowdsource.api.user.IUser;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginMessages;
import org.softwareFm.crowdsource.utilities.processors.AbstractLoginDataAccessor;

public class SignUpChecker extends AbstractLoginDataAccessor implements ISignUpChecker {

	private final ICrowdSourcedReadWriteApi readWriteApi;
	private final ICryptoGenerators cryptoGenerator;

	public SignUpChecker(DataSource dataSource, ICryptoGenerators cryptoGenerator, ICrowdSourcedReadWriteApi readWriteApi) {
		super(dataSource);
		this.cryptoGenerator = cryptoGenerator;
		this.readWriteApi = readWriteApi;
	}

	@Override
	public SignUpResult signUp(final String email, final String moniker, String salt, String passwordHash, final String softwareFmId) {
		int existingEmail = template.queryForInt(countUsersWithEmailSql, email);
		if (existingEmail != 0)
			return new SignUpResult(MessageFormat.format(LoginMessages.existingEmailAddress, email), null);
		int existingSoftwareFmId = template.queryForInt(countUsersWithSoftwareFmIdSql, email);
		if (existingSoftwareFmId != 0)
			return new SignUpResult(MessageFormat.format(LoginMessages.existingSoftwareFmId, softwareFmId), null);
		final String key = cryptoGenerator.userCrypto();
		template.update(addNewUserSql, email, salt, passwordHash, key, softwareFmId);
		readWriteApi.modifyUser(new ICallback<IUser>(){
			@Override
			public void process(IUser user) throws Exception {
				user.setUserProperty(softwareFmId, key, LoginConstants.emailKey, email);
				user.setUserProperty(softwareFmId, key, LoginConstants.monikerKey, moniker);
			}});
		return new SignUpResult(null, key);
	}

}