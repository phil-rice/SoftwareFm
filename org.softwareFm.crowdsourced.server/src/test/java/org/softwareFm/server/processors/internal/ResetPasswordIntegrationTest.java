package org.softwareFm.server.processors.internal;

import java.text.MessageFormat;

import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.constants.LoginMessages;
import org.softwareFm.server.processors.AbstractProcessorMockIntegrationTests;

public class ResetPasswordIntegrationTest extends AbstractProcessorMockIntegrationTests {

	public void testWithNoError() throws Exception {
		String magicString = "theMagicString";
		resetPassword(magicString, IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, MessageFormat.format(LoginMessages.passwordResetHtml, "theNewPassword")));

	}

}
