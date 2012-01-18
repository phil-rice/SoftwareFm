package org.softwareFm.server.processors.internal;

import java.text.MessageFormat;
import java.util.Map;

import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IPasswordResetter;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.utilities.strings.Strings;

public class ForgottonPasswordWebPageProcessor extends AbstractCommandProcessor {

	private final IPasswordResetter resetter;

	public ForgottonPasswordWebPageProcessor(IPasswordResetter resetter) {
		super(null, ServerConstants.GET, ServerConstants.passwordResetLinkPrefix);
		this.resetter = resetter;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		String magicString = Strings.lastSegment(actualUrl, "/");
		try {
			String newPassword = resetter.reset(magicString);
			return IProcessResult.Utils.processString(MessageFormat.format(ServerConstants.passwordResetHtml, newPassword));
		} catch (Exception e) {
			return IProcessResult.Utils.processString(ServerConstants.failedToResetPasswordHtml);
		}
	}

}
