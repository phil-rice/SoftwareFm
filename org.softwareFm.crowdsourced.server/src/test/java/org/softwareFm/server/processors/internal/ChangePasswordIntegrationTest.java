package org.softwareFm.server.processors.internal;

import java.util.concurrent.TimeUnit;

import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.constants.LoginConstants;
import org.softwareFm.server.constants.LoginMessages;
import org.softwareFm.server.processors.AbstractProcessorDatabaseIntegrationTests;
import org.softwareFm.utilities.exceptions.WrappedException;

public class ChangePasswordIntegrationTest extends AbstractProcessorDatabaseIntegrationTests {

	public void testSignupFollowedByChangePassword() throws Exception {
		String email = "a@b.com";

		String salt = makeSalt();
		signup(email, salt, "oldHash","someNewSoftwareFmId0");
		changePassword(email, "oldHash", "newHash");
		String newSalt = makeSalt();
		String emailSalt = requestEmailSalt(newSalt, email);
		login(email, newSalt, emailSalt, "newHash");
	}

	public void testFailsIfWrongOldHashAndCanStillLogin() {
		String email = "a@b.com";

		String salt = makeSalt();
		signup(email, salt, "oldHash","someNewSoftwareFmId0");

		changePassword(email, "wrongHash", "newHash", IResponseCallback.Utils.checkCallback(CommonConstants.notFoundStatusCode, LoginMessages.wrongPassword));
		String newSalt = makeSalt();
		String emailSalt = requestEmailSalt(newSalt, email);
		login(email, newSalt, emailSalt, "oldHash");

	}

	public void testSignupFollowedByOldPasswordFails() throws Exception {
		String email = "a@b.com";

		String salt = makeSalt();
		signup(email, salt, "oldHash", "someNewSoftwareFmId0");
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
