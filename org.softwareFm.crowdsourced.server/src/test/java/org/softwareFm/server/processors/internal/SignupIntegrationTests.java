package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.easymock.EasyMock;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.constants.LoginConstants;
import org.softwareFm.server.constants.LoginMessages;
import org.softwareFm.server.processors.AbstractProcessorMockIntegrationTests;
import org.softwareFm.utilities.collections.Sets;
import org.softwareFm.utilities.maps.Maps;

public class SignupIntegrationTests extends AbstractProcessorMockIntegrationTests {

	private final Map<String, Object> userDetails = Maps.stringObjectMap(LoginConstants.softwareFmIdKey, "someSoftwareFmId0");

	public void testMakeSaltThenSignUp() throws Exception {
		userMock.setUserProperty(userDetails, signUpCrypto, LoginConstants.emailKey, "someEmail");
		EasyMock.replay(userMock);

		String salt = "salt 0";
		getHttpClient().get(LoginConstants.makeSaltPrefix).execute(IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, salt)).get(); // salt won't be used but we want it removed
		assertEquals(salt, Sets.getOnly(saltProcessor.legalSalts));
		assertEquals(signUpCrypto, signup("someEmail", salt, "hash", "someSoftwareFmId0"));
		assertEquals(0, saltProcessor.legalSalts.size());

		EasyMock.verify(userMock);
	}

	public void testNeedsToUseSignupSalt() throws Exception {
		signup("someEmail", "some salt", "someHash", IResponseCallback.Utils.checkCallback(CommonConstants.notFoundStatusCode, LoginMessages.invalidSaltMessage));
	}

}
