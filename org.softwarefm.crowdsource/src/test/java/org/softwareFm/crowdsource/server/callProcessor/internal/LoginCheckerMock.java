/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.api.server.ILoginChecker;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.maps.Maps;

public class LoginCheckerMock implements ILoginChecker {
	public final List<String> emails = Lists.newList();
	public final List<String> passwordHashes = Lists.newList();
	private Map<String, String> map;

	public LoginCheckerMock(String crypto, String softwareFmId) {
		setResult(crypto, softwareFmId);
	}

	@Override
	public Map<String, String> login(String email, String passwordHash) {
		emails.add(email);
		passwordHashes.add(passwordHash);
		return map;
	}

	public void setResult(String crypto, String softwareFmId) {
		this.map = Maps.makeMap(LoginConstants.softwareFmIdKey, softwareFmId, LoginConstants.cryptoKey, crypto);
	}

	public void setResultToNull() {
		this.map = null;
		
	}
}