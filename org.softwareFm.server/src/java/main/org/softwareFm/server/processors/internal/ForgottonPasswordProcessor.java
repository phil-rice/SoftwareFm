package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IForgottonPasswordProcessor;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.server.processors.ISaltProcessor;
import org.softwareFm.utilities.strings.Strings;

public class ForgottonPasswordProcessor extends AbstractCommandProcessor {

	private final IForgottonPasswordProcessor forgottonPasswordProcessor;
	private final ISaltProcessor saltProcessor;

	public ForgottonPasswordProcessor(ISaltProcessor saltProcessor, IForgottonPasswordProcessor forgottonPasswordProcessor) {
		super(null, ServerConstants.POST, ServerConstants.forgottonPasswordPrefix);
		this.saltProcessor = saltProcessor;
		this.forgottonPasswordProcessor = forgottonPasswordProcessor;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		String salt = Strings.nullSafeToString(parameters.get(ServerConstants.saltKey));
		String email = Strings.nullSafeToString(parameters.get(ServerConstants.emailKey));
		saltProcessor.invalidateSalt(salt);
		String potentialError = forgottonPasswordProcessor.process(email);
		if (potentialError == null)
			return IProcessResult.Utils.processString("");
		else
			return IProcessResult.Utils.processError(ServerConstants.notFoundStatusCode, potentialError);
	}

}
