package org.softwareFm.server.processors.internal;

import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.server.processors.ISaltProcessor;
import org.softwareFm.server.processors.ISignUpChecker;
import org.softwareFm.server.processors.SignUpResult;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.runnable.Callables;
import org.softwareFm.utilities.strings.Strings;

public class SignupProcessor extends AbstractCommandProcessor {

	private final ISaltProcessor saltProcessor;
	private final ISignUpChecker checker;
	private final Callable<String> softwareFmIdGenerator;

	public SignupProcessor(ISignUpChecker checker, ISaltProcessor saltProcessor, Callable<String> softwareFmIdGenerator) {
		super(null, ServerConstants.POST, ServerConstants.signupPrefix);
		this.checker = checker;
		this.saltProcessor = saltProcessor;
		this.softwareFmIdGenerator = softwareFmIdGenerator;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		String salt = Strings.nullSafeToString(parameters.get(ServerConstants.sessionSaltKey));
		if (saltProcessor.invalidateSalt(salt)) {
			String email = Strings.nullSafeToString(parameters.get(ServerConstants.emailKey));
			String passwordHash = Strings.nullSafeToString(parameters.get(ServerConstants.passwordHashKey));
			String softwareFmId = Callables.call(softwareFmIdGenerator);
			SignUpResult result = checker.signUp(email, salt, passwordHash, softwareFmId);
			if (result.errorMessage != null)
				return IProcessResult.Utils.processError(ServerConstants.notFoundStatusCode, result.errorMessage);
			else
				return IProcessResult.Utils.processString(Json.mapToString(ServerConstants.softwareFmIdKey, softwareFmId, ServerConstants.cryptoKey, result.crypto));

		}
		return IProcessResult.Utils.processError(ServerConstants.notFoundStatusCode, ServerConstants.invalidSaltMessage);
	}

}
