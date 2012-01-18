package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IForgottonPasswordMailer;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.server.processors.ISaltProcessor;
import org.softwareFm.utilities.strings.Strings;

public class ForgottonPasswordProcessor extends AbstractCommandProcessor {

	private final IForgottonPasswordMailer forgottonPasswordMailer;
	private final ISaltProcessor saltProcessor;

	public ForgottonPasswordProcessor(ISaltProcessor saltProcessor, IForgottonPasswordMailer forgottonPasswordMailer) {
		super(null, ServerConstants.POST, ServerConstants.forgottonPasswordPrefix);
		this.saltProcessor = saltProcessor;
		this.forgottonPasswordMailer = forgottonPasswordMailer;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		String salt = Strings.nullSafeToString(parameters.get(ServerConstants.saltKey));
		String email = Strings.nullSafeToString(parameters.get(ServerConstants.emailKey));
		saltProcessor.invalidateSalt(salt);
		forgottonPasswordMailer.process(email);
		return IProcessResult.Utils.processString("");
	}

}
