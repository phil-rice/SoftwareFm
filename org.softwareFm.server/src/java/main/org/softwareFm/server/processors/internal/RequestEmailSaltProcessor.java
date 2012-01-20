package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IEmailSaltRequester;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.utilities.strings.Strings;

public class RequestEmailSaltProcessor extends AbstractCommandProcessor {

	private final IEmailSaltRequester saltRequester;

	public RequestEmailSaltProcessor(IEmailSaltRequester saltRequester) {
		super(null, ServerConstants.POST, ServerConstants.emailSaltPrefix);
		this.saltRequester = saltRequester;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		String email = Strings.nullSafeToString(parameters.get(ServerConstants.emailKey));
		String emailSalt = saltRequester.getSalt(email);
		if (emailSalt == null)
			return IProcessResult.Utils.processError(ServerConstants.notFoundStatusCode, ServerConstants.emailNotRecognised);
		else
			return IProcessResult.Utils.processString(emailSalt);
	}

}
