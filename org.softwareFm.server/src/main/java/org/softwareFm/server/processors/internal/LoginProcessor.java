/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.constants.LoginMessages;
import org.softwareFm.common.json.Json;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.server.processors.ILoginChecker;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.server.processors.ISaltProcessor;

public class LoginProcessor extends AbstractCommandProcessor {

	private final ILoginChecker checker;
	private final ISaltProcessor saltProcessor;

	public LoginProcessor(ISaltProcessor saltProcessor, ILoginChecker checker) {
		super(null, CommonConstants.POST, LoginConstants.loginCommandPrefix);
		this.saltProcessor = saltProcessor;
		this.checker = checker;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		String salt = Strings.nullSafeToString(parameters.get(LoginConstants.sessionSaltKey));
		saltProcessor.invalidateSalt(salt);
		String email = Strings.nullSafeToString(parameters.get(LoginConstants.emailKey));
		String passwordHash = Strings.nullSafeToString(parameters.get(LoginConstants.passwordHashKey));
		Map<String, String> map = checker.login(email, passwordHash);
		if (map == null)
			return IProcessResult.Utils.processError(CommonConstants.notFoundStatusCode, LoginMessages.emailPasswordMismatch);
		else {
			if (!map.containsKey(LoginConstants.softwareFmIdKey))
				throw new IllegalStateException(map.toString());
			if (!map.containsKey(LoginConstants.cryptoKey))
				throw new IllegalStateException(map.toString());
			if (map.size() != 2)
				throw new IllegalStateException(map.toString());
			String result = Json.toString(Maps.with(map, LoginConstants.emailKey, email));
			return IProcessResult.Utils.processString(result);
		}
	}

}