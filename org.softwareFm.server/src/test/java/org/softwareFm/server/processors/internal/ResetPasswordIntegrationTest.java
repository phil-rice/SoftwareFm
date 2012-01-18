package org.softwareFm.server.processors.internal;

import java.text.MessageFormat;

import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.server.ServerConstants;

public class ResetPasswordIntegrationTest extends AbstractProcessorMockIntegrationTests {

	public void testWithNoError() throws Exception {
		String magicString = "theMagicString";
		resetPassword(magicString, IResponseCallback.Utils.checkCallback(ServerConstants.okStatusCode, MessageFormat.format(ServerConstants.passwordResetHtml, "theNewPassword")));

	}

}
