/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.doers.internal;

import java.util.UUID;

import javax.sql.DataSource;

import org.softwareFm.crowdsource.api.server.IPasswordResetter;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.processors.AbstractLoginDataAccessor;
import org.springframework.dao.DataAccessException;

public class PasswordResetter extends AbstractLoginDataAccessor implements IPasswordResetter {

	public PasswordResetter(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public String reset(String magicString) {
		String newPassword = UUID.randomUUID().toString();
		try {
			String salt = template.queryForObject(selectSaltFromUserWithPasswordResetKeySql, String.class, magicString);
			String newDigest = Crypto.digest(salt, newPassword);
			template.update(updatePasswordAndClearResetKeySql, newDigest, magicString);
			return newPassword;
		} catch (DataAccessException e) {
			return null;
		}
	}

}