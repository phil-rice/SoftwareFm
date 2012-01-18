package org.softwareFm.server.processors.internal;

import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.collections.Sets;

public class SignupIntegrationTests extends AbstractProcessorMockIntegrationTests {

	public void testMakeSaltThenSignUp() throws Exception {
		String salt = "salt 0";
		client.get(ServerConstants.makeSaltPrefix).execute(IResponseCallback.Utils.checkCallback(ServerConstants.okStatusCode, salt)).get(); // salt won't be used but we want it removed
		assertEquals(salt, Sets.getOnly(saltProcessor.legalSalts));
		assertEquals("signUpCrypto", signup("someEmail", salt, "hash"));
		assertEquals(0, saltProcessor.legalSalts.size());
	}

	public void testNeedsToUseSignupSalt() throws Exception {
		signup("someEmail", "some salt", "someHash", IResponseCallback.Utils.checkCallback(ServerConstants.notFoundStatusCode, ServerConstants.invalidSaltMessage));
	}

}
