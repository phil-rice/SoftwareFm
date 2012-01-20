package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.server.processors.ISaltProcessor;
import org.softwareFm.server.processors.ISignUpChecker;
import org.softwareFm.server.processors.SignUpResult;
import org.softwareFm.utilities.strings.Strings;

public class SignupProcessor extends AbstractCommandProcessor {

	private final ISaltProcessor saltProcessor;
	private final ISignUpChecker checker;

	public SignupProcessor( ISignUpChecker checker, ISaltProcessor saltProcessor) {
		super(null, ServerConstants.POST, ServerConstants.signupPrefix);
		this.checker = checker;
		this.saltProcessor = saltProcessor;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		String salt = Strings.nullSafeToString(parameters.get(ServerConstants.sessionSaltKey));
		if (saltProcessor.invalidateSalt(salt)) {
			String email = Strings.nullSafeToString(parameters.get(ServerConstants.emailKey));
			String passwordHash = Strings.nullSafeToString(parameters.get(ServerConstants.passwordHashKey));
			SignUpResult result = checker.signUp(email, salt, passwordHash);
			if (result.errorMessage != null)
				return IProcessResult.Utils.processError(ServerConstants.notFoundStatusCode, result.errorMessage);
			else
				return IProcessResult.Utils.processString(result.crypto);

		}
		return IProcessResult.Utils.processError(ServerConstants.notFoundStatusCode, ServerConstants.invalidSaltMessage);
	}

}
