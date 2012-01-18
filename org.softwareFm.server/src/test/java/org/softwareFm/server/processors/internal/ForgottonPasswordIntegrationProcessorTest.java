package org.softwareFm.server.processors.internal;

import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.collections.Sets;

public class ForgottonPasswordIntegrationProcessorTest extends AbstractProcessorIntegrationTests {

	public void testSaltFollowedByForgottonPassword() throws Exception {
		String salt = "salt 0";
		client.get(ServerConstants.makeSaltPrefix).execute(IResponseCallback.Utils.checkCallback(ServerConstants.okStatusCode, salt)).get(); // salt won't be used but we want it removed
		assertEquals(salt, Sets.getOnly(saltProcessor.legalSalts));
		client.post(ServerConstants.forgottonPasswordPrefix).//
				addParam(ServerConstants.emailKey, "someEmail").//
				addParam(ServerConstants.saltKey, salt).//
				execute(IResponseCallback.Utils.checkCallback(ServerConstants.okStatusCode, "")).get();
		assertEquals(0, saltProcessor.legalSalts.size());
	}
	public void testSaltFollowedByUnknownError() throws Exception {
		String salt = "salt 0";
		client.get(ServerConstants.makeSaltPrefix).execute(IResponseCallback.Utils.checkCallback(ServerConstants.okStatusCode, salt)).get(); // salt won't be used but we want it removed
		assertEquals(salt, Sets.getOnly(saltProcessor.legalSalts));
		forgottonPasswordProcessor.setErrorMessage("some error message");
		client.post(ServerConstants.forgottonPasswordPrefix).//
				addParam(ServerConstants.emailKey, "someEmail").//
				addParam(ServerConstants.saltKey, salt).//
				execute(IResponseCallback.Utils.checkCallback(500, "class java.lang.RuntimeException/some error message")).get();
		assertEquals(0, saltProcessor.legalSalts.size());
	}

}
