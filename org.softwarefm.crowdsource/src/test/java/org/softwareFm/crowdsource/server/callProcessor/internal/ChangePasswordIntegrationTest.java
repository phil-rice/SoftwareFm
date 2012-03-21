/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.util.concurrent.TimeUnit;

import org.softwareFm.crowdsource.api.server.AbstractProcessorDatabaseIntegrationTests;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginMessages;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;

public class ChangePasswordIntegrationTest extends AbstractProcessorDatabaseIntegrationTests {

	public void testSignupFollowedByChangePassword() throws Exception {
		String email = "a@b.com";

		String salt = makeSalt();
		signup(email, salt, "someMoniker", "oldHash", "someNewSoftwareFmId0");
		changePassword(email, "oldHash", "newHash");
		String newSalt = makeSalt();
		String emailSalt = requestEmailSalt(newSalt, email);
		login(email, newSalt, emailSalt, "newHash");
	}

	public void testFailsIfWrongOldHashAndCanStillLogin() {
		String email = "a@b.com";

		String salt = makeSalt();
		signup(email, salt, "someMoniker", "oldHash", "someNewSoftwareFmId0");

		changePassword(email, "wrongHash", "newHash", IResponseCallback.Utils.checkCallback(CommonConstants.notFoundStatusCode, LoginMessages.wrongPassword));
		String newSalt = makeSalt();
		String emailSalt = requestEmailSalt(newSalt, email);
		login(email, newSalt, emailSalt, "oldHash");

	}

	public void testSignupFollowedByOldPasswordFails() throws Exception {
		String email = "a@b.com";

		String salt = makeSalt();
		signup(email, salt, "someMoniker", "oldHash", "someNewSoftwareFmId0");
		changePassword(email, "oldHash", "newHash");
		String newSalt = makeSalt();
		String emailSalt = requestEmailSalt(newSalt, email);
		login(email, newSalt, emailSalt, "oldHash", IResponseCallback.Utils.checkCallback(CommonConstants.notFoundStatusCode, LoginMessages.emailPasswordMismatch));

	}

	private void changePassword(String email, String oldHash, String newHash) {
		changePassword(email, oldHash, newHash, IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, LoginMessages.passwordChanged));
	}

	private void changePassword(String email, String oldHash, String newHash, IResponseCallback callback) {
		try {
			getHttpClient().post(LoginConstants.changePasswordPrefix).//
					addParam(LoginConstants.emailKey, email).//
					addParam(LoginConstants.passwordHashKey, oldHash).//
					addParam(LoginConstants.newPasswordHashKey, newHash).//
					execute(callback).get(CommonConstants.testTimeOutMs, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

}