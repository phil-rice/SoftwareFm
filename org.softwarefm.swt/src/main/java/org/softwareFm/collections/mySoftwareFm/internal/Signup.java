package org.softwareFm.collections.mySoftwareFm.internal;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.card.ICardData;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.configuration.ICardConfigurator;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.ICardDataStore;
import org.softwareFm.card.editors.ICardEditorCallback;
import org.softwareFm.card.editors.INamesAndValuesEditor;
import org.softwareFm.collections.mySoftwareFm.ILoginCallbacks;
import org.softwareFm.collections.mySoftwareFm.ILoginDisplayStrategy;
import org.softwareFm.collections.mySoftwareFm.ILoginStrategy;
import org.softwareFm.collections.mySoftwareFm.IShowMessage;
import org.softwareFm.collections.mySoftwareFm.ISignUp;
import org.softwareFm.collections.mySoftwareFm.ISignUpCallback;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.display.swt.Swts;

public class Signup implements ISignUp {
	private final String cardType = CardConstants.signupCardType;
	private final INamesAndValuesEditor content;

	public Signup(Composite parent, final CardConfig cardConfig, final String salt, String initialEmail, final ILoginStrategy strategy, final ILoginDisplayStrategy loginDisplayStrategy, final ISignUpCallback callback) {
		String title = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, cardType, CardConstants.signupTitle);
		content = INamesAndValuesEditor.Utils.editor(parent, cardConfig, cardType, title, "", Maps.stringObjectLinkedMap(LoginConstants.emailKey, initialEmail), Arrays.asList(//
				INamesAndValuesEditor.Utils.text(cardConfig, cardType, "email"),//
				INamesAndValuesEditor.Utils.password(cardConfig, cardType, "password"),//
				INamesAndValuesEditor.Utils.password(cardConfig, cardType, "confirmPassword")),//
				new ICardEditorCallback() {
					@Override
					public void ok(ICardData cardData) {
						Map<String, Object> data = cardData.data();
						String password = getPassword(data);
						String passwordHash = Crypto.digest(salt, password);
						final String email = getEmail(cardData.data());
						strategy.signup(email, salt, passwordHash, callback);
					}

					private String get(Map<String, Object> data, String key) {
						return Strings.nullSafeToString(data.get(key));
					}

					@Override
					public void cancel(ICardData cardData) {
						loginDisplayStrategy.showLogin(salt, getEmail(cardData.data()));
					}

					@Override
					public boolean canOk(Map<String, Object> data) {
						boolean emailOk = Strings.isEmail(getEmail(data));
						boolean passwordOk = getPassword(data).length() > 0 && getPassword(data).equals(getConfirmPassword(data));
						return emailOk && passwordOk;
					}

					private String getEmail(Map<String, Object> data) {
						return get(data, "email");
					}

					private String getPassword(Map<String, Object> data) {
						return get(data, "password");
					}

					private String getConfirmPassword(Map<String, Object> data) {
						return get(data, "confirmPassword");
					}

				});
		Composite buttonComposite = content.getButtonComposite();
		Swts.Buttons.makeImageButtonAtStart(buttonComposite, cardConfig.imageFn, CardConstants.forgotPasswordImage, new Runnable() {
			@Override
			public void run() {
				loginDisplayStrategy.showForgotPassword(salt, getEmail());
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
		Swts.Show.display(Signup.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite parent) throws Exception {
				CardConfig cardConfig = ICardConfigurator.Utils.basicConfigurator().configure(parent.getDisplay(), new CardConfig(ICardFactory.Utils.noCardFactory(), ICardDataStore.Utils.mock()));
				String salt = "someSalt";
				return (Composite) new Signup(parent, cardConfig, salt, "initialEmail", ILoginStrategy.Utils.sysoutLoginStrategy(), //
						ILoginDisplayStrategy.Utils.sysoutDisplayStrategy(), //
						ILoginCallbacks.Utils.showMessageCallbacks(cardConfig, IShowMessage.Utils.sysout())).getControl();
			}
		});
	}

}
