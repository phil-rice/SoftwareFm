package org.softwareFm.server.processors.internal;

import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.collections.Sets;

public class LoginIntegrationTests extends AbstractProcessorIntegrationTests  {

	public void testMakeSaltLogin() throws Exception {
		String salt = "salt 0";
		client.get(ServerConstants.makeSaltPrefix).execute(IResponseCallback.Utils.checkCallback(ServerConstants.okStatusCode, salt)).get(); // salt won't be used but we want it removed
		assertEquals(salt, Sets.getOnly(saltProcessor.legalSalts));
		client.post(ServerConstants.loginCommandPrefix).//
				addParam(ServerConstants.emailKey, "someEmail").//
				addParam(ServerConstants.saltKey, salt).//
				addParam(ServerConstants.passwordHashKey, "someHash").//
				execute(IResponseCallback.Utils.checkMapCallback(ServerConstants.okStatusCode, ServerConstants.cryptoKey, "loginCrypto", ServerConstants.emailKey, "someEmail")).get();
		assertEquals(0, saltProcessor.legalSalts.size());
	}

	public void testRemovesSaltAndLogsins() throws Exception {
		String salt = "salt 0";
		client.get(ServerConstants.makeSaltPrefix).execute(IResponseCallback.Utils.checkCallback(ServerConstants.okStatusCode, salt)).get(); // salt won't be used but we want it removed
		assertEquals(salt, Sets.getOnly(saltProcessor.legalSalts));

		loginChecker.setCrypto(null);// will fail to login

		client.post(ServerConstants.loginCommandPrefix).//
				addParam(ServerConstants.emailKey, "someEmail").//
				addParam(ServerConstants.saltKey, salt).//
				addParam(ServerConstants.passwordHashKey, "someHash").//
				execute(IResponseCallback.Utils.checkCallback(ServerConstants.notFoundStatusCode, ServerConstants.emailPasswordMismatch)).get();
		assertEquals(0, saltProcessor.legalSalts.size()); // removed salt anyway
	}

}
