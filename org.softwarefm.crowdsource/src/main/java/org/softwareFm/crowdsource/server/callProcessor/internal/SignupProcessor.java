/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.text.MessageFormat;
import java.util.Map;

import org.softwareFm.crowdsource.api.IIdAndSaltGenerator;
import org.softwareFm.crowdsource.api.server.AbstractCallProcessor;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.api.server.ISaltProcessor;
import org.softwareFm.crowdsource.api.server.ISignUpChecker;
import org.softwareFm.crowdsource.api.server.SignUpResult;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginMessages;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.strings.Strings;

public class SignupProcessor extends AbstractCallProcessor {

	private final ISaltProcessor saltProcessor;
	private final ISignUpChecker checker;
	private final IIdAndSaltGenerator idGenerators;

	public SignupProcessor(ISignUpChecker checker, ISaltProcessor saltProcessor, IIdAndSaltGenerator idAndSaltGenerators) {
		super(CommonConstants.POST, LoginConstants.signupPrefix);
		this.checker = checker;
		this.saltProcessor = saltProcessor;
		this.idGenerators = idAndSaltGenerators;
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
			String softwareFmId =idGenerators.makeNewUserId();
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