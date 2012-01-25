package org.softwareFm.server.processors.internal;

import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.collections.Sets;

public class LoginIntegrationTests extends AbstractProcessorMockIntegrationTests {

	public void testMakeSaltLogin() throws Exception {
		String sessionSalt = "salt 0";
		String email = "someEmail";
		getHttpClient().get(ServerConstants.makeSaltPrefix).execute(IResponseCallback.Utils.checkCallback(ServerConstants.okStatusCode, sessionSalt)).get(); // salt won't be used but we want it removed
		assertEquals(sessionSalt, Sets.getOnly(saltProcessor.legalSalts));
		String emailSalt = requestEmailSalt(sessionSalt, email);
		login(email, sessionSalt, emailSalt, "someHash", IResponseCallback.Utils.checkMapCallback(//
				ServerConstants.okStatusCode, ServerConstants.cryptoKey, "loginCrypto", ServerConstants.emailKey, "someEmail", ServerConstants.softwareFmIdKey, "loginCheckersSoftwareFmId"));
		assertEquals(0, saltProcessor.legalSalts.size());
	}

	public void testRemovesSaltAndLogsins() throws Exception {
		String email = "someEmail";
		String sessionSalt = "salt 0";
		getHttpClient().get(ServerConstants.makeSaltPrefix).execute(IResponseCallback.Utils.checkCallback(ServerConstants.okStatusCode, sessionSalt)).get(); // salt won't be used but we want it removed
		assertEquals(sessionSalt, Sets.getOnly(saltProcessor.legalSalts));

		loginChecker.setResultToNull();// will fail to login
		String emailSalt = requestEmailSalt(sessionSalt, email);
		login(email, sessionSalt, emailSalt, "someHash", IResponseCallback.Utils.checkCallback(ServerConstants.notFoundStatusCode, ServerConstants.emailPasswordMismatch));
		assertEquals(0, saltProcessor.legalSalts.size()); // removed salt anyway
	}

}
