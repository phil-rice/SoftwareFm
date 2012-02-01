package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.constants.LoginConstants;
import org.softwareFm.server.constants.LoginMessages;
import org.softwareFm.server.processors.IEmailSaltRequester;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.utilities.strings.Strings;

public class RequestEmailSaltProcessor extends AbstractCommandProcessor {

	private final IEmailSaltRequester saltRequester;

	public RequestEmailSaltProcessor(IEmailSaltRequester saltRequester) {
		super(null, CommonConstants.POST, CommonConstants.emailSaltPrefix);
		this.saltRequester = saltRequester;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		String email = Strings.nullSafeToString(parameters.get(LoginConstants.emailKey));
		String emailSalt = saltRequester.getSalt(email);
		if (emailSalt == null)
			return IProcessResult.Utils.processError(CommonConstants.notFoundStatusCode, LoginMessages.emailNotRecognised);
		else
			return IProcessResult.Utils.processString(emailSalt);
	}

}
