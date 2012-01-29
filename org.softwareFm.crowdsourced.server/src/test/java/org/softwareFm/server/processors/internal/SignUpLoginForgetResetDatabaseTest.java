package org.softwareFm.server.processors.internal;

import java.text.MessageFormat;

import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.constants.LoginMessages;
import org.softwareFm.server.processors.AbstractProcessorDatabaseIntegrationTests;
import org.softwareFm.utilities.crypto.Crypto;

public class SignUpLoginForgetResetDatabaseTest extends AbstractProcessorDatabaseIntegrationTests {
	final String email = "someEmail1@a";

	public void testSignupPutsSaltCryptoHashAndSoftwareFmIdInDatabase() {
		String initialSalt = makeSalt();
		String crypto1 = signup(email, initialSalt, "hash", "someNewSoftwareFmId0");
		String crypto = crypto1;

		assertEquals(1, template.queryForInt("select count(*) from users where email=?", email));
		assertEquals(initialSalt, template.queryForObject("select salt from users where email=?", String.class, email));
		assertEquals("hash", template.queryForObject("select password from users where email=?", String.class, email));
		assertEquals(crypto, template.queryForObject("select crypto from users where email=?", String.class, email));
		assertEquals("someNewSoftwareFmId0", template.queryForObject("select softwarefmid from users where email=?",String.class, email));
	}

	public void testCannotSignupWithDuplicateEmail() {
		String salt1 = makeSalt();
		signup(email, salt1, "hash", "someNewSoftwareFmId0");

		String salt2 = makeSalt();
		signup(email, salt2, "hash", IResponseCallback.Utils.checkCallback(CommonConstants.notFoundStatusCode, MessageFormat.format(LoginMessages.existingEmailAddress, email)));
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
		String crypto = signup(email, salt1, "hash", "someNewSoftwareFmId0");
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
		signup(email, salt1, "hash", "someNewSoftwareFmId0");

		String sessionSalt = makeSalt();
		String emailSalt = requestEmailSalt(sessionSalt, email);
		login(email, sessionSalt, emailSalt, "hash2", IResponseCallback.Utils.checkCallback(CommonConstants.notFoundStatusCode, LoginMessages.emailPasswordMismatch));
	}

	public void testForgotPassword() {
		String signupSalt = makeSalt();
		String crypto = signup(email, signupSalt, "startHash", "someNewSoftwareFmId0");

		
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
		signup(email, salt1, "hash", "someNewSoftwareFmId0");

		String resetKey = "someFakeKey";
		resetPassword(resetKey, IResponseCallback.Utils.checkCallback(CommonConstants.okStatusCode, LoginMessages.failedToResetPasswordHtml));
		System.out.println();
	}

	public void testForgotPasswordAndUseLinkTwice() {
		String salt1 = makeSalt();
		signup(email, salt1, "hash", "someNewSoftwareFmId0");
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