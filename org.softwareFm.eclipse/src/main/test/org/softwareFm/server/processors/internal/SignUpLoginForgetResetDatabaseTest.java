/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.processors.internal;

import java.text.MessageFormat;

import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginMessages;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.server.processors.AbstractProcessorDatabaseIntegrationTests;

public class SignUpLoginForgetResetDatabaseTest extends AbstractProcessorDatabaseIntegrationTests {
	final String email = "someEmail1@a.com";

	public void testSignupPutsSaltCryptoHashAndSoftwareFmIdInDatabase() {
		String initialSalt = makeSalt();
		String crypto1 = signup(email, initialSalt, "someMoniker", "hash", "someNewSoftwareFmId0");
		String crypto = crypto1;

		assertEquals(1, template.queryForInt("select count(*) from users where email=?", email));
		assertEquals(initialSalt, template.queryForObject("select salt from users where email=?", String.class, email));
		assertEquals("hash", template.queryForObject("select password from users where email=?", String.class, email));
		assertEquals(crypto, template.queryForObject("select crypto from users where email=?", String.class, email));
		assertEquals("someNewSoftwareFmId0", template.queryForObject("select softwarefmid from users where email=?", String.class, email));
	}

	public void testCannotSignupWithDuplicateEmail() {
		String salt1 = makeSalt();
		signup(email, salt1, "someMoniker", "hash", "someNewSoftwareFmId0");

		String salt2 = makeSalt();
		signup(email, salt2, "someMoniker", "hash", IResponseCallback.Utils.checkCallback(CommonConstants.notFoundStatusCode, MessageFormat.format(LoginMessages.existingEmailAddress, email)));
	}

	public void testLoginAfterSignUp() {
		String crypto = initialSignup();

		String sessionSalt = makeSalt();
		String emailSalt = requestEmailSalt(sessionSalt, email);

		String actualCrypto = login(email, sessionSalt, emailSalt, "hash");
		assertEquals(crypto, actualCrypto);
	}

	private String initialSignup() {
		String salt1 = makeSalt();
		String crypto = signup(email, salt1, "someMoniker", "hash", "someNewSoftwareFmId0");
		return crypto;
	}

	public void testLoginWithoutEmailHashFails() {
		initialSignup();
		String sessionSalt = makeSalt();
		String emailSalt = requestEmailSalt(sessionSalt, email);
		login(email, sessionSalt, emailSalt, "wronghash", IResponseCallback.Utils.checkCallback(CommonConstants.notFoundStatusCode, LoginMessages.emailPasswordMismatch));
	}

	public void testLoginWithWrongHash() {
		String salt1 = makeSalt();
		signup(email, salt1, "someMoniker", "hash", "someNewSoftwareFmId0");

		String sessionSalt = makeSalt();
		String emailSalt = requestEmailSalt(sessionSalt, email);
		login(email, sessionSalt, emailSalt, "hash2", IResponseCallback.Utils.checkCallback(CommonConstants.notFoundStatusCode, LoginMessages.emailPasswordMismatch));
	}

	public void testForgotPassword() {
		String signupSalt = makeSalt();
		String crypto = signup(email, signupSalt, "someMoniker", "startHash", "someNewSoftwareFmId0");

		String sessionSalt1 = makeSalt();
		forgotPassword(email, sessionSalt1, IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, ""));

		String resetKey = getPasswordResetKeyFor(email);

		String hash = resetPasswordAndGetHash(signupSalt, resetKey);

		String sessionSalt2 = makeSalt();
		String emailSalt = requestEmailSalt(sessionSalt2, email);
		String actualCrypto = login(email, sessionSalt2, emailSalt, hash);
		assertEquals(crypto, actualCrypto);

		assertNull(getPasswordResetKeyFor(email));
	}

	public void testForgotPasswordWhenNotSetup() {
		String salt1 = makeSalt();
		signup(email, salt1, "someMoniker", "hash", "someNewSoftwareFmId0");

		String resetKey = "someFakeKey";
		resetPassword(resetKey, IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, LoginMessages.failedToResetPasswordHtml));
		System.out.println();
	}

	public void testForgotPasswordAndUseLinkTwice() {
		String salt1 = makeSalt();
		signup(email, salt1, "someMoniker", "hash", "someNewSoftwareFmId0");
		String resetKey = getPasswordResetKeyFor(email);
		resetPasswordAndGetHash(salt1, resetKey);
		resetPassword(resetKey, IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, LoginMessages.failedToResetPasswordHtml));
	}

	private String resetPasswordAndGetHash(String signupSalt, String resetKey) {
		StringCallback callback = new StringCallback();
		resetPassword(resetKey, callback);
		int startIndex = callback.string.indexOf(": ") + 2;
		int endIndex = callback.string.indexOf("</html");
		String password = callback.string.substring(startIndex, endIndex);

		String hash = Crypto.digest(signupSalt, password);
		return hash;
	}

}