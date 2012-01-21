package org.softwareFm.server.processors.internal;

import java.util.concurrent.TimeUnit;

import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.exceptions.WrappedException;

public class ChangePasswordIntegrationTest extends AbstractProcessorDatabaseIntegrationTests {

	public void testSignupFollowedByChangePasswrd() throws Exception {
		String email = "a@b.com";

		String salt = makeSalt();
		signup(email, salt, "oldHash");
		changePassword(email, "oldHash", "newHash");
		String newSalt = makeSalt();
		String emailSalt = requestEmailSalt(newSalt, email);
		login(email, newSalt, emailSalt, "newHash");
	}

	public void testFailsIfWrongOldHashAndCanStillLogin() {
		String email = "a@b.com";

		String salt = makeSalt();
		signup(email, salt, "oldHash");

		changePassword(email, "wrongHash", "newHash", IResponseCallback.Utils.checkCallback(ServerConstants.notFoundStatusCode, ServerConstants.wrongPassword));
		String newSalt = makeSalt();
		String emailSalt = requestEmailSalt(newSalt, email);
		login(email, newSalt, emailSalt, "oldHash");

	}

	public void testSignupFollowedByOldPasswordFails() throws Exception {
		String email = "a@b.com";

		String salt = makeSalt();
		signup(email, salt, "oldHash");
		changePassword(email, "oldHash", "newHash");
		String newSalt = makeSalt();
		String emailSalt = requestEmailSalt(newSalt, email);
		login(email, newSalt, emailSalt, "oldHash", IResponseCallback.Utils.checkCallback(ServerConstants.notFoundStatusCode, ServerConstants.emailPasswordMismatch));

	}

	private void changePassword(String email, String oldHash, String newHash) {
		changePassword(email, oldHash, newHash, IResponseCallback.Utils.checkCallback(ServerConstants.okStatusCode, ServerConstants.passwordChanged));
	}

	private void changePassword(String email, String oldHash, String newHash, IResponseCallback callback) {
		try {
			client.post(ServerConstants.changePasswordPrefix).//
					addParam(ServerConstants.emailKey, email).//
					addParam(ServerConstants.passwordHashKey, oldHash).//
					addParam(ServerConstants.newPasswordHashKey, newHash).//
					execute(callback).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

}
