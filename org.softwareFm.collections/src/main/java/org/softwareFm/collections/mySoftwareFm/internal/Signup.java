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
import org.softwareFm.collections.mySoftwareFm.ILoginStrategy;
import org.softwareFm.collections.mySoftwareFm.ISignUp;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.crypto.Crypto;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class Signup implements ISignUp {
	private final String cardType = CardConstants.signupCardType;
	private final INamesAndValuesEditor content;

	public Signup(Composite parent, CardConfig cardConfig, final String salt, final String cryptoKey, final ILoginStrategy strategy) {
		String title = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, cardType, CardConstants.signupTitle);
		content = INamesAndValuesEditor.Utils.editor(parent, cardConfig, cardType, title, "", Maps.stringObjectLinkedMap(), Arrays.asList(//
				INamesAndValuesEditor.Utils.text(cardConfig, cardType, "email"),//
				INamesAndValuesEditor.Utils.text(cardConfig, cardType, "password"),//
				INamesAndValuesEditor.Utils.text(cardConfig, cardType, "confirmPassword")),//
				new ICardEditorCallback() {
					@Override
					public void ok(ICardData cardData) {
						Map<String, Object> data = cardData.data();
						String password = getPassword(data);
						String passwordHash = Crypto.aesEncrypt(cryptoKey, Maps.stringObjectLinkedMap("password", password, "salt", salt).toString());
						strategy.signup(getEmail(cardData.data()), salt, cryptoKey, passwordHash);
					}

					private String get(Map<String, Object> data, String key) {
						return Strings.nullSafeToString(data.get(key));
					}

					@Override
					public void cancel(ICardData cardData) {
						strategy.showLogin(salt);
					}

					@Override
					public boolean canOk(Map<String, Object> data) {
						boolean emailOk = getEmail(data).length() > 0;
						boolean passwordOk = getPassword(data).length() > 0&&getPassword(data).equals(getConfirmPassword(data));
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
		Swts.Buttons.makePushButtonAtStart(buttonComposite, CardConfig.resourceGetter(content), CardConstants.forgotPasswordButtonTitle, new Runnable() {
			@Override
			public void run() {
				strategy.showForgotPassword(salt);
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
		Swts.Show.display(Signup.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite parent) throws Exception {
				CardConfig cardConfig = ICardConfigurator.Utils.basicConfigurator().configure(parent.getDisplay(), new CardConfig(ICardFactory.Utils.noCardFactory(), ICardDataStore.Utils.mock()));
				String key = "someKey";
				String salt = "someSalt";
				return (Composite) new Signup(parent, cardConfig, salt, key, ILoginStrategy.Utils.sysoutLoginStrategy()).getControl();
			}
		});
	}

}
