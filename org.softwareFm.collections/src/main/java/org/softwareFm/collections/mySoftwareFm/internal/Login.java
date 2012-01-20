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
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class Login implements ILogin {
	private final String cardType = CardConstants.loginCardType;
	private final INamesAndValuesEditor content;

	public Login(Composite parent, final CardConfig cardConfig, final String sessionSalt, final ILoginStrategy loginStrategy, final ILoginDisplayStrategy loginDisplayStrategy, final ILoginCallback callback) {
		String title = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, cardType, CardConstants.loginTitle);
		content = INamesAndValuesEditor.Utils.editor(parent, cardConfig, cardType, title, "", Maps.stringObjectLinkedMap(), Arrays.asList(//
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
						loginDisplayStrategy.showLogin(sessionSalt);
					}

					@Override
					public boolean canOk(Map<String, Object> data) {
						boolean emailOk = getEmail(data).length() > 0;
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
		Swts.Buttons.makePushButtonAtStart(buttonComposite, CardConfig.resourceGetter(content), CardConstants.signUpButtonTitle, new Runnable() {
			@Override
			public void run() {
				loginDisplayStrategy.showSignup(sessionSalt);

			}
		});
		Swts.Buttons.makePushButtonAtStart(buttonComposite, CardConfig.resourceGetter(content), CardConstants.forgotPasswordButtonTitle, new Runnable() {
			@Override
			public void run() {
				loginDisplayStrategy.showForgotPassword(sessionSalt);

			}
		});
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
				return (Composite) new Login(parent, cardConfig, UUID.randomUUID().toString(), ILoginStrategy.Utils.sysoutLoginStrategy(), ILoginDisplayStrategy.Utils.sysoutDisplayStrategy(), ILoginCallbacks.Utils.showMessageCallbacks(cardConfig, IShowMessage.Utils.sysout())).getControl();
			}
		});
	}

}
