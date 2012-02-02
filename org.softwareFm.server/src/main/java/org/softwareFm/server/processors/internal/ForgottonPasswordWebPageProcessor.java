package org.softwareFm.server.processors.internal;

import java.text.MessageFormat;
import java.util.Map;

import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.constants.LoginMessages;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.server.processors.IPasswordResetter;
import org.softwareFm.server.processors.IProcessResult;

public class ForgottonPasswordWebPageProcessor extends AbstractCommandProcessor {

	private final IPasswordResetter resetter;

	public ForgottonPasswordWebPageProcessor(IPasswordResetter resetter) {
		super(null, CommonConstants.GET, LoginConstants.passwordResetLinkPrefix);
		this.resetter = resetter;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		String magicString = Strings.lastSegment(actualUrl, "/");
		try {
			String newPassword = resetter.reset(magicString);
			if (newPassword == null)
				return IProcessResult.Utils.processString(MessageFormat.format(LoginMessages.failedToResetPasswordHtml, newPassword));
			else
				return IProcessResult.Utils.processString(MessageFormat.format(LoginMessages.passwordResetHtml, newPassword));
		} catch (Exception e) {
			return IProcessResult.Utils.processString(LoginMessages.failedToResetPasswordHtml);
		}
	}

}
