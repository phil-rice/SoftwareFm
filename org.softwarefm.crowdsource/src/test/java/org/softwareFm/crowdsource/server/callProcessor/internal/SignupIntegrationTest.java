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

public class SignupIntegrationTest extends AbstractProcessorMockIntegrationTests {

	private final String email = "a@b.com";

	public void testMakeSaltThenSignUp() throws Exception {
		String salt = "salt 0";
		getHttpClient().get(LoginConstants.makeSaltPrefix).execute(IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, salt)).get(); // salt won't be used but we want it removed
		assertEquals(salt, Sets.getOnly(saltProcessor.legalSalts));
		assertEquals(signUpCrypto, signup(email, salt, "someMoniker", "hash", "someSoftwareFmId0"));
		assertEquals(0, saltProcessor.legalSalts.size());
	}

	public void testNeedsToUseSignupSalt() throws Exception {
		signup(email, "some salt", "someMoniker", "someHash", IResponseCallback.Utils.checkCallback(CommonConstants.notFoundStatusCode, LoginMessages.invalidSaltMessage));
	}

}