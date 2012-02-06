package org.softwareFm.server.processors.internal;

import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.common.IUser;
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
			String moniker = Strings.nullSafeToString(parameters.get(LoginConstants.monikerKey));
			String passwordHash = Strings.nullSafeToString(parameters.get(LoginConstants.passwordHashKey));
			String softwareFmId = Callables.call(softwareFmIdGenerator);
			if (email == null || moniker == null || passwordHash == null || softwareFmId == null)
				throw new IllegalArgumentException(parameters.toString());
			SignUpResult result = checker.signUp(email, salt, passwordHash, softwareFmId);
			if (result.errorMessage != null)
				return IProcessResult.Utils.processError(CommonConstants.notFoundStatusCode, result.errorMessage);
			else {
				user.setUserProperty(softwareFmId, result.crypto, LoginConstants.emailKey, email);
				user.setUserProperty(softwareFmId, result.crypto, LoginConstants.monikerKey, moniker);
				return IProcessResult.Utils.processString(Json.mapToString(LoginConstants.softwareFmIdKey, softwareFmId, LoginConstants.cryptoKey, result.crypto));
			}

		}
		return IProcessResult.Utils.processError(CommonConstants.notFoundStatusCode, LoginMessages.invalidSaltMessage);
	}

}
