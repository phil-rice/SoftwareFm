package org.softwareFm.server.processors.internal;

import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.common.collections.Sets;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.constants.LoginMessages;
import org.softwareFm.server.processors.AbstractProcessorMockIntegrationTests;

public class LoginIntegrationTests extends AbstractProcessorMockIntegrationTests {

	public void testMakeSaltLogin() throws Exception {
		String sessionSalt = "salt 0";
		String email = "someEmail";
		getHttpClient().get(LoginConstants.makeSaltPrefix).execute(IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, sessionSalt)).get(); // salt won't be used but we want it removed
		assertEquals(sessionSalt, Sets.getOnly(saltProcessor.legalSalts));
		String emailSalt = requestEmailSalt(sessionSalt, email);
		login(email, sessionSalt, emailSalt, "someHash", IResponseCallback.Utils.checkMapCallback(//
				CommonConstants.okStatusCode, LoginConstants.cryptoKey, "loginCrypto", LoginConstants.emailKey, "someEmail", LoginConstants.softwareFmIdKey, "loginCheckersSoftwareFmId"));
		assertEquals(0, saltProcessor.legalSalts.size());
	}

	public void testRemovesSaltAndLogsins() throws Exception {
		String email = "someEmail";
		String sessionSalt = "salt 0";
		getHttpClient().get(LoginConstants.makeSaltPrefix).execute(IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, sessionSalt)).get(); // salt won't be used but we want it removed
		assertEquals(sessionSalt, Sets.getOnly(saltProcessor.legalSalts));

		loginChecker.setResultToNull();// will fail to login
		String emailSalt = requestEmailSalt(sessionSalt, email);
		login(email, sessionSalt, emailSalt, "someHash", IResponseCallback.Utils.checkCallback(CommonConstants.notFoundStatusCode, LoginMessages.emailPasswordMismatch));
		assertEquals(0, saltProcessor.legalSalts.size()); // removed salt anyway
	}

}
