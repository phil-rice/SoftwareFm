/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.processors.internal;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.constants.LoginMessages;
import org.softwareFm.common.json.Json;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.server.processors.ISaltProcessor;
import org.softwareFm.server.processors.ISignUpChecker;
import org.softwareFm.server.processors.SignUpResult;

public class SignupProcessor extends AbstractCommandProcessor {

	private final ISaltProcessor saltProcessor;
	private final ISignUpChecker checker;
	private final Callable<String> softwareFmIdGenerator;

	public SignupProcessor(ISignUpChecker checker, ISaltProcessor saltProcessor, Callable<String> softwareFmIdGenerator) {
		super(null, CommonConstants.POST, LoginConstants.signupPrefix);
		this.checker = checker;
		this.saltProcessor = saltProcessor;
		this.softwareFmIdGenerator = softwareFmIdGenerator;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		checkForParameter(parameters, LoginConstants.sessionSaltKey, LoginConstants.emailKey, LoginConstants.monikerKey, LoginConstants.passwordHashKey);
		String email = Strings.nullSafeToString(parameters.get(LoginConstants.emailKey));
		String moniker = Strings.nullSafeToString(parameters.get(LoginConstants.monikerKey));
		String passwordHash = Strings.nullSafeToString(parameters.get(LoginConstants.passwordHashKey));
		if (!Strings.isEmail(email))
			throw new IllegalArgumentException(MessageFormat.format(LoginMessages.invalidEmail, email));

		String salt = Strings.nullSafeToString(parameters.get(LoginConstants.sessionSaltKey));
		if (saltProcessor.invalidateSalt(salt)) {
			String softwareFmId = Callables.call(softwareFmIdGenerator);
			if (email == null || moniker == null || passwordHash == null || softwareFmId == null)
				throw new IllegalArgumentException(parameters.toString());
			SignUpResult result = checker.signUp(email, moniker, salt, passwordHash, softwareFmId);
			if (result.errorMessage != null)
				return IProcessResult.Utils.processError(CommonConstants.notFoundStatusCode, result.errorMessage);
			else {
				return IProcessResult.Utils.processString(Json.mapToString(LoginConstants.softwareFmIdKey, softwareFmId, LoginConstants.cryptoKey, result.crypto));
			}

		}
		return IProcessResult.Utils.processError(CommonConstants.notFoundStatusCode, LoginMessages.invalidSaltMessage);
	}

}