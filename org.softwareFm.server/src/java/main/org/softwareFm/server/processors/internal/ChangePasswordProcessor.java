package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IPasswordChanger;
import org.softwareFm.server.processors.IProcessResult;

public class ChangePasswordProcessor extends AbstractCommandProcessor {

	private final IPasswordChanger passwordChanger;

	public ChangePasswordProcessor(IPasswordChanger passwordChanger) {
		super(null, ServerConstants.POST, ServerConstants.changePasswordPrefix);
		this.passwordChanger = passwordChanger;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		String email = (String) parameters.get(ServerConstants.emailKey);
		String oldHash = (String) parameters.get(ServerConstants.passwordHashKey);
		String newHash = (String) parameters.get(ServerConstants.newPasswordHashKey);
		if (passwordChanger.changePassword(email, oldHash, newHash))
			return IProcessResult.Utils.processString(ServerConstants.passwordChanged);
		else
			return IProcessResult.Utils.processError(ServerConstants.notFoundStatusCode, ServerConstants.wrongPassword);

	}
}
