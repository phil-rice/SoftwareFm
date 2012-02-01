package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.constants.LoginConstants;
import org.softwareFm.server.constants.LoginMessages;
import org.softwareFm.server.processors.IPasswordChanger;
import org.softwareFm.server.processors.IProcessResult;

public class ChangePasswordProcessor extends AbstractCommandProcessor {

	private final IPasswordChanger passwordChanger;

	public ChangePasswordProcessor(IPasswordChanger passwordChanger) {
		super(null, CommonConstants.POST, LoginConstants.changePasswordPrefix);
		this.passwordChanger = passwordChanger;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		String email = (String) parameters.get(LoginConstants.emailKey);
		String oldHash = (String) parameters.get(LoginConstants.passwordHashKey);
		String newHash = (String) parameters.get(LoginConstants.newPasswordHashKey);
		if (passwordChanger.changePassword(email, oldHash, newHash))
			return IProcessResult.Utils.processString(LoginMessages.passwordChanged);
		else
			return IProcessResult.Utils.processError(CommonConstants.notFoundStatusCode, LoginMessages.wrongPassword);

	}
}
