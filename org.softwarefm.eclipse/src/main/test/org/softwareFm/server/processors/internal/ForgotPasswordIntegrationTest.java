package org.softwareFm.server.processors.internal;

import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.common.collections.Sets;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.server.processors.AbstractProcessorMockIntegrationTests;

public class ForgotPasswordIntegrationTest extends AbstractProcessorMockIntegrationTests {

	public void testMakeSaltThenForgotPassword() throws Exception {
		String salt = "salt 0";
		getHttpClient().get(LoginConstants.makeSaltPrefix).execute(IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, salt)).get(); // salt won't be used but we want it removed
		
		assertEquals(salt, Sets.getOnly(saltProcessor.legalSalts));
		
		forgotPassword("someEmail", salt, IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, ""));
		
		assertEquals(0, saltProcessor.legalSalts.size());
	}


}
