package org.softwareFm.collections.mySoftwareFm;

import java.util.UUID;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.crypto.Crypto;

public interface ILoginStrategy {

	void showLogin(String salt);

	void showForgotPassword(String salt);

	void showSignup(String salt);

	void forgotPassword(String email, IForgotPasswordCallback callback);

	void login(String email, String password, ILoginCallback callback);

	void requestSalt(IRequestSaltCallback callback);

	void signup(String email, String salt, String cryptoKey, String passwordHash, ISignUpCallback callback);

	public static class Utils {
		public static ILoginStrategy mock(final CardConfig cardConfig, final Composite holder, final boolean ok, final ILoginCallbacks callback) {
			ILoginStrategy loginStrategy = new ILoginStrategy() {
				@Override
				public void requestSalt(IRequestSaltCallback callback) {
					if (ok)
						callback.saltReceived(UUID.randomUUID().toString());
					else
						callback.problemGettingSalt("some failure message");
				}

				@Override
				public void signup(String email, String salt, String cryptoKey, String passwordHash, ISignUpCallback callback) {
					System.out.println("Signing up email: " + email + ", " + salt + " hash: " + passwordHash);
					System.out.println("Which decodes to: " + Crypto.aesDecrypt(cryptoKey, passwordHash));
					if (ok)
						callback.signedUp(email);
					else
						callback.failed(email, "some failure message");
				}

				@Override
				public void showSignup(String salt) {
					Swts.removeAllChildren(holder);
					System.out.println("Would have sent salt to SFM: " + salt);
					String crypto = Crypto.makeKey();
					System.out.println("And got reply: " + crypto);
					ISignUp.Utils.signUp(holder, cardConfig, salt, crypto, this, callback);
					holder.layout();
				}

				@Override
				public void showLogin(String salt) {
					Swts.removeAllChildren(holder);
					ILogin.Utils.login(holder, cardConfig, salt, this, callback);
					holder.layout();
				}

				@Override
				public void showForgotPassword(String salt) {
					Swts.removeAllChildren(holder);
					IForgotPassword.Utils.forgotPassword(holder, cardConfig, salt, this,callback);
					holder.layout();
				}

				@Override
				public void login(String email, String password, ILoginCallback callback) {
					System.out.println("Logging in");
					if (ok)
						callback.loggedIn(email, Crypto.makeKey());
					else
						callback.failedToLogin(email, "some failure message");
				}

				@Override
				public void forgotPassword(String email, IForgotPasswordCallback callback) {
					System.out.println("Logging in");
					if (ok)
						callback.emailSent(email);
					else
						callback.failedToSend(email, "some reason");
				}

			};
			return loginStrategy;

		}

		public static ILoginStrategy sysoutLoginStrategy() {
			return new ILoginStrategy() {

				@Override
				public void showLogin(String salt) {
					System.out.println("Show login");
				}

				@Override
				public void showForgotPassword(String salt) {
					System.out.println("Show Forgot Password");
				}

				@Override
				public void showSignup(String salt) {
					System.out.println("Show Sign Up");

				}

				@Override
				public void forgotPassword(String email, IForgotPasswordCallback callback) {
					System.out.println("Sending 'forgot password' to server");
					callback.emailSent(email);
				}

				@Override
				public void login(String email, String password, ILoginCallback callback) {
					String key = Crypto.makeKey();
					System.out.println("Sending 'login' to server, would have received key: " + key);
					callback.loggedIn(email, key);
				}

				@Override
				public void requestSalt(IRequestSaltCallback callback) {
					System.out.println("Requesting salt");
					callback.saltReceived(UUID.randomUUID().toString());
				}

				@Override
				public void signup(String email, String salt, String cryptoKey, String passwordHash, ISignUpCallback callback) {
					System.out.println("Signing up: " + email + ", " + salt + ", " + passwordHash);
					callback.signedUp(email);
				}

			};

		}
	}

}
