package org.softwareFm.server.processors.internal;

import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.constants.LoginConstants;
import org.softwareFm.server.constants.LoginMessages;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.server.processors.ISaltProcessor;
import org.softwareFm.server.processors.ISignUpChecker;
import org.softwareFm.server.processors.SignUpResult;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.runnable.Callables;
import org.softwareFm.utilities.strings.Strings;

public class SignupProcessor extends AbstractCommandProcessor {

	private final ISaltProcessor saltProcessor;
	private final ISignUpChecker checker;
	private final Callable<String> softwareFmIdGenerator;
	private final IUser user;

	public SignupProcessor(ISignUpChecker checker, ISaltProcessor saltProcessor, Callable<String> softwareFmIdGenerator, IUser user) {
		super(null, CommonConstants.POST, LoginConstants.signupPrefix);
		this.checker = checker;
		this.saltProcessor = saltProcessor;
		this.softwareFmIdGenerator = softwareFmIdGenerator;
		this.user = user;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		String salt = Strings.nullSafeToString(parameters.get(LoginConstants.sessionSaltKey));
		if (saltProcessor.invalidateSalt(salt)) {
			String email = Strings.nullSafeToString(parameters.get(LoginConstants.emailKey));
			String passwordHash = Strings.nullSafeToString(parameters.get(LoginConstants.passwordHashKey));
			String softwareFmId = Callables.call(softwareFmIdGenerator);
			SignUpResult result = checker.signUp(email, salt, passwordHash, softwareFmId);
			if (result.errorMessage != null)
				return IProcessResult.Utils.processError(CommonConstants.notFoundStatusCode, result.errorMessage);
			else {
				user.saveUserDetails(Maps.stringObjectMap(LoginConstants.cryptoKey, result.crypto, LoginConstants.softwareFmIdKey, softwareFmId), Maps.stringObjectMap(LoginConstants.emailKey, email));
				return IProcessResult.Utils.processString(Json.mapToString(LoginConstants.softwareFmIdKey, softwareFmId, LoginConstants.cryptoKey, result.crypto));
			}

		}
		return IProcessResult.Utils.processError(CommonConstants.notFoundStatusCode, LoginMessages.invalidSaltMessage);
	}

}
