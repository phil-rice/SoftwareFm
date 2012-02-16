/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.processors.internal;

import java.util.List;

import org.softwareFm.common.collections.Lists;
import org.softwareFm.server.processors.ISignUpChecker;
import org.softwareFm.server.processors.SignUpResult;

public class SignUpCheckerMock implements ISignUpChecker {

	public final List<String> emails = Lists.newList();
	public final List<String> salts = Lists.newList();
	public final List<String> passwordHashes = Lists.newList();
	public final List<String> softwareFmIds = Lists.newList();
	private final String errorMessage;
	private final String crypto;

	public SignUpCheckerMock(String errorMessage, String crypto) {
		this.errorMessage = errorMessage;
		this.crypto = crypto;
	}

	@Override
	public SignUpResult signUp(String email, String moniker, String salt, String passwordHash, String softwareFmId) {
		emails.add(email);
		salts.add(salt);
		passwordHashes.add(passwordHash);
		softwareFmIds.add(softwareFmId);
		return new SignUpResult(errorMessage, crypto);
	}

}