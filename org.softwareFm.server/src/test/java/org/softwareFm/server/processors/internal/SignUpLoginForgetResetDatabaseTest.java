package org.softwareFm.server.processors.internal;

import java.text.MessageFormat;

import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.crypto.Crypto;

public class SignUpLoginForgetResetDatabaseTest extends AbstractProcessorDatabaseIntegrationTests {
	final String email = "someEmail1@a";

	public void testSignupPutsSaltCryptoAndHashInDatabase() {
		String salt = makeSalt();
		String crypto = signup(email, salt, "hash");

		assertEquals(1, template.queryForInt("select count(*) from users where email=?", email));
		assertEquals(salt, template.queryForObject("select salt from users where email=?", String.class, email));
		assertEquals("hash", template.queryForObject("select password from users where email=?", String.class, email));
		assertEquals(crypto, template.queryForObject("select crypto from users where email=?", String.class, email));

	}

	public void testCannotSignupWithDuplicateEmail() {
		String salt1 = makeSalt();
		signup(email, salt1, "hash");

		String salt2 = makeSalt();
		signup(email, salt2, "hash", IResponseCallback.Utils.checkCallback(ServerConstants.notFoundStatusCode, MessageFormat.format(ServerConstants.existingEmailAddress, email)));
	}

	public void testLoginAfterSignUp() {
		String salt1 = makeSalt();
		String crypto = signup(email, salt1, "hash");

		String salt2 = makeSalt();
		MapCallback callback = new MapCallback();
		login(email, salt2, "hash", callback);
		assertEquals(email, callback.map.get(ServerConstants.emailKey));
		assertEquals(crypto, callback.map.get(ServerConstants.cryptoKey));
	}

	public void testLoginWithoutSignupFails() {
		String salt = makeSalt();
		login(email, salt, "hash", IResponseCallback.Utils.checkCallback(ServerConstants.notFoundStatusCode, ServerConstants.emailPasswordMismatch));
	}

	public void testLoginWithWrongHash() {
		String salt1 = makeSalt();
		signup(email, salt1, "hash");

		String salt2 = makeSalt();
		login(email, salt2, "hash2", IResponseCallback.Utils.checkCallback(ServerConstants.notFoundStatusCode, ServerConstants.emailPasswordMismatch));
	}

	public void testForgotPassword() {
		String salt1 = makeSalt();
		String crypto = signup(email, salt1, "hash");

		forgotPassword(email, salt1, IResponseCallback.Utils.checkCallback(ServerConstants.okStatusCode, ""));
		String resetKey = getPasswordResetKeyFor(email);

		String hash = resetPasswordAndGetHash(salt1, resetKey);

		String salt2 = makeSalt();
		MapCallback mapCallback = new MapCallback();
		login(email, salt2, hash, mapCallback);
		assertEquals(crypto, mapCallback.map.get(ServerConstants.cryptoKey));
		assertEquals(email, mapCallback.map.get(ServerConstants.emailKey));
		assertNull(getPasswordResetKeyFor(email));
	}

	public void testForgotPasswordWhenNotSetup() {
		String salt1 = makeSalt();
		signup(email, salt1, "hash");

		String resetKey = "someFakeKey";
		resetPassword(resetKey, IResponseCallback.Utils.checkCallback(ServerConstants.okStatusCode, ServerConstants.failedToResetPasswordHtml));
		System.out.println();
	}

	public void testForgotPasswordAndUseLinkTwice() {
		String salt1 = makeSalt();
		signup(email, salt1, "hash");
		String resetKey = getPasswordResetKeyFor(email);
		resetPasswordAndGetHash(salt1, resetKey);
		resetPassword(resetKey, IResponseCallback.Utils.checkCallback(ServerConstants.okStatusCode, ServerConstants.failedToResetPasswordHtml));
	}

	private String resetPasswordAndGetHash(String salt1, String resetKey) {
		StringCallback callback = new StringCallback();
		resetPassword(resetKey, callback);
		int startIndex = callback.string.indexOf(": ") + 2;
		int endIndex = callback.string.indexOf("</html");
		String password = callback.string.substring(startIndex, endIndex);

		String hash = Crypto.digest(salt1, password);
		return hash;
	}

}
