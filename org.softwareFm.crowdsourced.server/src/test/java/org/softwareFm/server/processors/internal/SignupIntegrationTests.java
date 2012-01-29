package org.softwareFm.server.processors.internal;

import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.constants.LoginMessages;
import org.softwareFm.server.processors.AbstractProcessorMockIntegrationTests;
import org.softwareFm.utilities.collections.Sets;

public class SignupIntegrationTests extends AbstractProcessorMockIntegrationTests {

	public void testMakeSaltThenSignUp() throws Exception {
		String salt = "salt 0";
		getHttpClient().get(CommonConstants.makeSaltPrefix).execute(IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, salt)).get(); // salt won't be used but we want it removed
		assertEquals(salt, Sets.getOnly(saltProcessor.legalSalts));
		assertEquals("signUpCrypto", signup("someEmail", salt, "hash", "someSoftwareFmId0"));
		assertEquals(0, saltProcessor.legalSalts.size());
		assertEquals(1, userMock.getUserDetailsCount.get());
	}

	public void testNeedsToUseSignupSalt() throws Exception {
		signup("someEmail", "some salt", "someHash", IResponseCallback.Utils.checkCallback(CommonConstants.notFoundStatusCode, LoginMessages.invalidSaltMessage));
	}

}
