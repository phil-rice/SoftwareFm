package org.softwareFm.server.processors.internal;

import java.text.MessageFormat;

import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.server.ServerConstants;

public class ResetPasswordIntegrationTest extends AbstractProcessorIntegrationTests {

	public void testWithNoError() throws Exception {
		
		client.get(ServerConstants.passwordResetLinkPrefix+"/thisMagicString").//
				addParam(ServerConstants.emailKey, "someEmail").//
				execute(IResponseCallback.Utils.checkCallback(ServerConstants.okStatusCode, MessageFormat.format(ServerConstants.passwordResetHtml, "theNewPassword"))).get();

	}

}
