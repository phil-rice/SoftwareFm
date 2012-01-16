package org.softwareFm.collections.mySoftwareFm;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.crypto.Crypto;

public interface ILoginStrategy {

	public void showLogin(String salt);

	public void showForgotPassword(String salt);

	public void showSignup(String salt);

	public void login(String email, String password);

	public void forgotPassword(String email);

	void signup(String email, String salt, String cryptoKey, String passwordHash);

	public static class Utils {
		public static ILoginStrategy mock(final CardConfig cardConfig, final Composite holder) {
			ILoginStrategy loginStrategy = new ILoginStrategy() {

				@Override
				public void signup(String email, String salt, String cryptoKey, String passwordHash) {
					System.out.println("Signing up email: " + email +", " + salt +" hash: " + passwordHash);
					System.out.println("Which decodes to: " + Crypto.aesDecrypt(cryptoKey, passwordHash));
				}

				@Override
				public void showSignup(String salt) {
					Swts.removeAllChildren(holder);
					System.out.println("Would have sent salt to SFM: " + salt);
					String crypto = Crypto.makeKey();
					System.out.println("And got reply: " + crypto);
					ISignUp.Utils.signUp(holder, cardConfig, salt, crypto, this);
					holder.layout();
				}

				@Override
				public void showLogin(String salt) {
					Swts.removeAllChildren(holder);
					ILogin.Utils.login(holder, cardConfig, salt, this);
					holder.layout();
				}

				@Override
				public void showForgotPassword(String salt) {
					Swts.removeAllChildren(holder);
					IForgotPassword.Utils.forgotPassword(holder, cardConfig, salt, this);
					holder.layout();
				}

				@Override
				public void login(String email, String password) {
				}

				@Override
				public void forgotPassword(String email) {
				}
			};
			return loginStrategy;

		}

		public static ILoginStrategy sysoutLoginStrategy() {
			return new ILoginStrategy() {
				@Override
				public void signup(String email, String salt, String cryptoKey, String passwordHash) {
					System.out.println("Signup: " + email + ", " + salt + ", " + passwordHash);
					System.out.println("       unhashed: " + Crypto.aesDecrypt(cryptoKey, passwordHash));
				}

				@Override
				public void showSignup(String salt) {
					System.out.println("showSignup");
				}

				@Override
				public void showLogin(String salt) {
					System.out.println("showLogin");
				}

				@Override
				public void showForgotPassword(String salt) {
					System.out.println("showForgotPassword");
				}

				@Override
				public void login(String email, String password) {
					System.out.println("login: " + email + ", " + password);
				}

				@Override
				public void forgotPassword(String email) {
					System.out.println("forgotPassword: " + email);
				}
			};

		}
	}

}
