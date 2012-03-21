/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import org.softwareFm.crowdsource.api.server.AbstractProcessorMockIntegrationTests;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.utilities.collections.Sets;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginMessages;

public class LoginIntegrationTest extends AbstractProcessorMockIntegrationTests {
	private final String email = "a@b.com";

	public void testMakeSaltLogin() throws Exception {
		String sessionSalt = "salt 0";
		String email = "a@b.com";
		getHttpClient().get(LoginConstants.makeSaltPrefix).execute(IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, sessionSalt)).get(); // salt won't be used but we want it removed
		assertEquals(sessionSalt, Sets.getOnly(saltProcessor.legalSalts));
		String emailSalt = requestEmailSalt(sessionSalt, email);
		login(email, sessionSalt, emailSalt, "someHash", IResponseCallback.Utils.checkMapCallback(//
				CommonConstants.okStatusCode, LoginConstants.cryptoKey, "loginCrypto", LoginConstants.emailKey, email, LoginConstants.softwareFmIdKey, "loginCheckersSoftwareFmId"));
		assertEquals(0, saltProcessor.legalSalts.size());
	}

	public void testRemovesSaltAndLogsins() throws Exception {
		String sessionSalt = "salt 0";
		getHttpClient().get(LoginConstants.makeSaltPrefix).execute(IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, sessionSalt)).get(); // salt won't be used but we want it removed
		assertEquals(sessionSalt, Sets.getOnly(saltProcessor.legalSalts));

		loginChecker.setResultToNull();// will fail to login
		String emailSalt = requestEmailSalt(sessionSalt, email);
		login(email, sessionSalt, emailSalt, "someHash", IResponseCallback.Utils.checkCallback(CommonConstants.notFoundStatusCode, LoginMessages.emailPasswordMismatch));
		assertEquals(0, saltProcessor.legalSalts.size()); // removed salt anyway
	}

}