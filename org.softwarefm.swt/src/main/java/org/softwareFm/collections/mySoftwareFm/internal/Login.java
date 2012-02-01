package org.softwareFm.collections.mySoftwareFm.internal;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.card.ICardData;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.configuration.ICardConfigurator;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.editors.ICardEditorCallback;
import org.softwareFm.card.editors.INamesAndValuesEditor;
import org.softwareFm.collections.mySoftwareFm.ILogin;
import org.softwareFm.collections.mySoftwareFm.ILoginCallback;
import org.softwareFm.collections.mySoftwareFm.ILoginCallbacks;
import org.softwareFm.collections.mySoftwareFm.ILoginDisplayStrategy;
import org.softwareFm.collections.mySoftwareFm.ILoginStrategy;
import org.softwareFm.collections.mySoftwareFm.IRequestSaltCallback;
import org.softwareFm.collections.mySoftwareFm.IShowMessage;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.display.swt.Swts;

public class Login implements ILogin {
	private final String cardType = CardConstants.loginCardType;
	final INamesAndValuesEditor content;

	public Login(Composite parent, final CardConfig cardConfig, final String sessionSalt, String initialEmail, final ILoginStrategy loginStrategy, final ILoginDisplayStrategy loginDisplayStrategy, final ILoginCallback callback) {
		String title = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, cardType, CardConstants.loginTitle);
		content = INamesAndValuesEditor.Utils.editor(parent, cardConfig, cardType, title, "", Maps.stringObjectLinkedMap(LoginConstants.emailKey, initialEmail), Arrays.asList(//
				INamesAndValuesEditor.Utils.text(cardConfig, cardType, "email"),//
				INamesAndValuesEditor.Utils.password(cardConfig, cardType, "password")),//
				new ICardEditorCallback() {
					@Override
					public void ok(ICardData cardData) {
						final Map<String, Object> data = cardData.data();
						final String email = getEmail(cardData.data());
						loginStrategy.requestEmailSalt(email, sessionSalt, new IRequestSaltCallback() {
							@Override
							public void saltReceived(String emailSalt) {
								loginStrategy.login(email, sessionSalt, emailSalt, getPassword(data), callback);
							}

							@Override
							public void problemGettingSalt(String message) {
								callback.failedToLogin(email, message);
							}
						});
					}

					private String get(Map<String, Object> data, String key) {
						return Strings.nullSafeToString(data.get(key));
					}

					@Override
					public void cancel(ICardData cardData) {
						loginDisplayStrategy.showLogin(sessionSalt, getEmail(cardData.data()));
					}

					@Override
					public boolean canOk(Map<String, Object> data) {
						String email = getEmail(data);
						boolean emailOk = Strings.isEmail(email);
						boolean passwordOk = getPassword(data).length() > 0;
						return emailOk && passwordOk;
					}

					private String getEmail(Map<String, Object> data) {
						return get(data, "email");
					}

					private String getPassword(Map<String, Object> data) {
						return get(data, "password");
					}

				});
		Composite buttonComposite = content.getButtonComposite();
		Swts.Buttons.makeImageButtonAtStart(buttonComposite, cardConfig.imageFn, CardConstants.signUpImage, new Runnable() {
			@Override
			public void run() {
				loginDisplayStrategy.showSignup(sessionSalt, getEmail());
			}
		});
		Swts.Buttons.makeImageButtonAtStart(buttonComposite, cardConfig.imageFn, CardConstants.forgotPasswordImage, new Runnable() {
			@Override
			public void run() {
				loginDisplayStrategy.showForgotPassword(sessionSalt, getEmail());
			}
		});
	}

	private String getEmail() {
		return (String) content.data().get(LoginConstants.emailKey);
	}

	@Override
	public Composite getComposite() {
		return content.getComposite();
	}

	@Override
	public Control getControl() {
		return content.getControl();
	}

	public static void main(String[] args) {
		Swts.Show.display(Login.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite parent) throws Exception {
				CardConfig cardConfig = ICardConfigurator.Utils.cardConfigForTests(parent.getDisplay());
				String salt = UUID.randomUUID().toString();
				ILoginStrategy sysoutLoginStrategy = ILoginStrategy.Utils.sysoutLoginStrategy();
				ILoginDisplayStrategy loginDisplayStrategy = ILoginDisplayStrategy.Utils.sysoutDisplayStrategy();
				ILoginCallbacks loginCallbacks = ILoginCallbacks.Utils.showMessageCallbacks(cardConfig, IShowMessage.Utils.sysout());
				return (Composite) new Login(parent, cardConfig, salt, "initialEmail", sysoutLoginStrategy, loginDisplayStrategy, loginCallbacks).getControl();
			}
		});
	}

}
