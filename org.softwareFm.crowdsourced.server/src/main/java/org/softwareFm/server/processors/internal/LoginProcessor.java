package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.constants.LoginConstants;
import org.softwareFm.server.constants.LoginMessages;
import org.softwareFm.server.processors.ILoginChecker;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.server.processors.ISaltProcessor;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Strings;

public class LoginProcessor extends AbstractCommandProcessor {

	private final ILoginChecker checker;
	private final ISaltProcessor saltProcessor;

	public LoginProcessor(ISaltProcessor saltProcessor, ILoginChecker checker) {
		super(null, CommonConstants.POST, CommonConstants.loginCommandPrefix);
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
