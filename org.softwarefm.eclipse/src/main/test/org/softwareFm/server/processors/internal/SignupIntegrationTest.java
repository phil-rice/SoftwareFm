package org.softwareFm.server.processors.internal;

import org.easymock.EasyMock;
import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.common.collections.Sets;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.constants.LoginMessages;
import org.softwareFm.server.processors.AbstractProcessorMockIntegrationTests;

public class SignupIntegrationTest extends AbstractProcessorMockIntegrationTests {

	public void testMakeSaltThenSignUp() throws Exception {
		EasyMock.replay(userMock);//no calls

		String salt = "salt 0";
		getHttpClient().get(LoginConstants.makeSaltPrefix).execute(IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, salt)).get(); // salt won't be used but we want it removed
		assertEquals(salt, Sets.getOnly(saltProcessor.legalSalts));
		assertEquals(signUpCrypto, signup("someEmail", salt, "someMoniker", "hash", "someSoftwareFmId0"));
		assertEquals(0, saltProcessor.legalSalts.size());

		EasyMock.verify(userMock);
	}

	public void testNeedsToUseSignupSalt() throws Exception {
		signup("someEmail", "some salt", "someMoniker", "someHash", IResponseCallback.Utils.checkCallback(CommonConstants.notFoundStatusCode, LoginMessages.invalidSaltMessage));
	}

}
