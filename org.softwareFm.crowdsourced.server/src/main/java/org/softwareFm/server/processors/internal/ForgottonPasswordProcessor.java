package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.constants.LoginConstants;
import org.softwareFm.server.processors.IForgottonPasswordMailer;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.server.processors.ISaltProcessor;
import org.softwareFm.utilities.strings.Strings;

public class ForgottonPasswordProcessor extends AbstractCommandProcessor {

	private final IForgottonPasswordMailer forgottonPasswordMailer;
	private final ISaltProcessor saltProcessor;

	public ForgottonPasswordProcessor(ISaltProcessor saltProcessor, IForgottonPasswordMailer forgottonPasswordMailer) {
		super(null, CommonConstants.POST, CommonConstants.forgottonPasswordPrefix);
		this.saltProcessor = saltProcessor;
		this.forgottonPasswordMailer = forgottonPasswordMailer;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		String salt = Strings.nullSafeToString(parameters.get(LoginConstants.sessionSaltKey));
		String email = Strings.nullSafeToString(parameters.get(LoginConstants.emailKey));
		saltProcessor.invalidateSalt(salt);
		String magicString = forgottonPasswordMailer.process(email);
		System.out.println("Magic String: " + magicString);
		return IProcessResult.Utils.processString("");
	}

}
