package org.softwareFm.collections.mySoftwareFm.internal;

import java.util.UUID;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.composites.TextInBorder;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.configuration.ICardConfigurator;
import org.softwareFm.collections.mySoftwareFm.IForgotPassword;
import org.softwareFm.collections.mySoftwareFm.IForgotPasswordCallback;
import org.softwareFm.collections.mySoftwareFm.ILogin;
import org.softwareFm.collections.mySoftwareFm.ILoginCallback;
import org.softwareFm.collections.mySoftwareFm.ILoginCallbacks;
import org.softwareFm.collections.mySoftwareFm.ILoginStrategy;
import org.softwareFm.collections.mySoftwareFm.IRequestSaltCallback;
import org.softwareFm.collections.mySoftwareFm.IShowMessage;
import org.softwareFm.collections.mySoftwareFm.ISignUp;
import org.softwareFm.collections.mySoftwareFm.ISignUpCallback;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.crypto.Crypto;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;

public class LoginSample {

	public static void main(String[] args) {
		Swts.Show.display(LoginSample.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				final CardConfig cardConfig = ICardConfigurator.Utils.cardConfigForTests(from.getDisplay());
				final Composite holder = new Composite(from, SWT.NULL);
				final boolean ok = true;
				final ILoginCallbacks callback = ILoginCallbacks.Utils.showMessageCallbacks(cardConfig, new IShowMessage() {
					@Override
					public void showMessage(String cardType, String title, String message) {
						Swts.removeAllChildren(holder);
						Functions.call(TextInBorder.makeTextFromString(SWT.WRAP | SWT.READ_ONLY, cardConfig, "Login", title, message), holder);
						holder.layout();
					}
				});
				ILoginStrategy loginStrategy = new ILoginStrategy() {

					@Override
					public void signup(String email, String sessionSalt, String cryptoKey, String passwordHash, ISignUpCallback callback) {
						System.out.println("Signing up email: " + email + ", " + sessionSalt + " hash: " + passwordHash);
						System.out.println("Which decodes to: " + Crypto.aesDecrypt(cryptoKey, passwordHash));
						if (ok)
							callback.signedUp(email);
						else
							callback.failed(email, "some failure message");
					}

					@Override
					public void showSignup(String sessionSalt) {
						Swts.removeAllChildren(holder);
						System.out.println("Would have sent salt to SFM: " + sessionSalt);
						String crypto = Crypto.makeKey();
						System.out.println("And got reply: " + crypto);
						ISignUp.Utils.signUp(holder, cardConfig, sessionSalt, crypto, this, callback);
						holder.layout();
					}

					@Override
					public void showLogin(String sessionSalt) {
						Swts.removeAllChildren(holder);
						ILogin.Utils.login(holder, cardConfig, sessionSalt, this, callback);
						holder.layout();
					}

					@Override
					public void showForgotPassword(String sessionSalt) {
						Swts.removeAllChildren(holder);
						IForgotPassword.Utils.forgotPassword(holder, cardConfig, sessionSalt, this, callback);
						holder.layout();
					}

					@Override
					public void login(String email, String sessionSalt, String emailSalt, String password, ILoginCallback callback) {
						System.out.println("Trying to login: " + email + ", " + password);
						if (ok)
							callback.loggedIn(email, Crypto.makeKey());
						else
							callback.failedToLogin(email, "some failure message");
					}

					@Override
					public void forgotPassword(String email, String sessionSalt, IForgotPasswordCallback callback) {
						System.out.println("Sending forgot password message to " + email+", with session salt " + sessionSalt);
						if (ok)
							callback.emailSent(email);
						else
							callback.failedToSend(email, "some failure message");
					}

					@Override
					public void requestSessionSalt(IRequestSaltCallback callback) {
						if (ok)
							callback.saltReceived(UUID.randomUUID().toString());
						else
							callback.problemGettingSalt("some failure message");
					}

					@Override
					public void requestEmailSalt(String email, String sessionSalt, IRequestSaltCallback callback) {
						if (ok)
							callback.saltReceived(UUID.randomUUID().toString());
						else
							callback.problemGettingSalt("some failure message");
					}
				};
				loginStrategy.showLogin(UUID.randomUUID().toString());
				holder.setLayout(new FillLayout());
				return holder;
			}

		});
	}
}
