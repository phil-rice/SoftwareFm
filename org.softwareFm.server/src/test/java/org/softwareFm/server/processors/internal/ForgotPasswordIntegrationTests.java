package org.softwareFm.server.processors.internal;

import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.collections.Sets;

public class ForgotPasswordIntegrationTests extends AbstractProcessorMockIntegrationTests {

	public void testMakeSaltThenForgotPassword() throws Exception {
		String salt = "salt 0";
		client.get(ServerConstants.makeSaltPrefix).execute(IResponseCallback.Utils.checkCallback(ServerConstants.okStatusCode, salt)).get(); // salt won't be used but we want it removed
		
		assertEquals(salt, Sets.getOnly(saltProcessor.legalSalts));
		
		forgotPassword("someEmail", salt, IResponseCallback.Utils.checkCallback(ServerConstants.okStatusCode, ""));
		
		assertEquals(0, saltProcessor.legalSalts.size());
	}


}
