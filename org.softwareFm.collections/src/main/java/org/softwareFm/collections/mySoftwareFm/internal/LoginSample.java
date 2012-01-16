package org.softwareFm.collections.mySoftwareFm.internal;

import java.util.UUID;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.configuration.ICardConfigurator;
import org.softwareFm.collections.mySoftwareFm.IForgotPassword;
import org.softwareFm.collections.mySoftwareFm.ILogin;
import org.softwareFm.collections.mySoftwareFm.ILoginStrategy;
import org.softwareFm.collections.mySoftwareFm.ISignUp;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.crypto.Crypto;
import org.softwareFm.utilities.functions.IFunction1;

public class LoginSample {

	public static void main(String[] args) {
		Swts.Show.display(LoginSample.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				final CardConfig cardConfig = ICardConfigurator.Utils.cardConfigForTests(from.getDisplay());
				final Composite holder = new Composite(from, SWT.NULL);
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
				loginStrategy.showLogin(UUID.randomUUID().toString());
				holder.setLayout(new FillLayout());
				return holder;
			}

		});
	}
}
