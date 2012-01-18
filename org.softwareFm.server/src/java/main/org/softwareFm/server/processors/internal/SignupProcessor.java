package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.server.processors.ISaltProcessor;
import org.softwareFm.server.processors.ISignUpChecker;
import org.softwareFm.server.processors.SignUpResult;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Strings;

public class SignupProcessor extends AbstractCommandProcessor {

	private final ISaltProcessor saltProcessor;
	private final ISignUpChecker checker;

	public SignupProcessor(IGitServer server, ISignUpChecker checker, ISaltProcessor saltProcessor) {
		super(server, ServerConstants.POST, ServerConstants.signupPrefix);
		this.checker = checker;
		this.saltProcessor = saltProcessor;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		String salt = Strings.nullSafeToString(parameters.get(ServerConstants.saltKey));
		if (saltProcessor.checkAndInvalidateSalt(salt)) {
			String email = Strings.nullSafeToString(parameters.get(ServerConstants.emailKey));
			String passwordHash = Strings.nullSafeToString(parameters.get(ServerConstants.passwordHashKey));
			SignUpResult result = checker.signUp(email, salt, passwordHash);
			if (result.errorMessage != null)
				return IProcessResult.Utils.processError(ServerConstants.notFoundStatusCode, result.errorMessage);
			else
				return IProcessResult.Utils.processString(Json.toString(Maps.stringObjectMap(ServerConstants.emailKey, email, ServerConstants.cryptoKey, result.crypto)));

		}
		return IProcessResult.Utils.processError(ServerConstants.notFoundStatusCode, ServerConstants.invalidSaltMessage);
	}

}
