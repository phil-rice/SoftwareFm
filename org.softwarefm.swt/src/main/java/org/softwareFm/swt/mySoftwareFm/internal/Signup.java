package org.softwareFm.swt.mySoftwareFm.internal;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.editors.ICardEditorCallback;
import org.softwareFm.swt.editors.INamesAndValuesEditor;
import org.softwareFm.swt.mySoftwareFm.ILoginDisplayStrategy;
import org.softwareFm.swt.mySoftwareFm.ILoginStrategy;
import org.softwareFm.swt.mySoftwareFm.ISignUp;
import org.softwareFm.swt.mySoftwareFm.ISignUpCallback;
import org.softwareFm.swt.swt.Swts;

public class Signup implements ISignUp {
	private final String cardType = CardConstants.signupCardType;
	private final INamesAndValuesEditor content;

	public Signup(Composite parent, final CardConfig cardConfig, final String salt, String initialEmail, final ILoginStrategy strategy, final ILoginDisplayStrategy loginDisplayStrategy, final ISignUpCallback callback) {
		String title = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, cardType, CardConstants.signupTitle);
		content = INamesAndValuesEditor.Utils.editor(parent, cardConfig, cardType, title, "", Maps.stringObjectLinkedMap(LoginConstants.emailKey, initialEmail), Arrays.asList(//
				INamesAndValuesEditor.Utils.text(cardConfig, cardType, LoginConstants.emailKey),//
				INamesAndValuesEditor.Utils.text(cardConfig, cardType, LoginConstants.monikerKey),//
				INamesAndValuesEditor.Utils.password(cardConfig, cardType, LoginConstants.passwordKey),//
				INamesAndValuesEditor.Utils.password(cardConfig, cardType, LoginConstants.confirmPasswordKey)),//
				new ICardEditorCallback() {
					@Override
					public void ok(ICardData cardData) {
						Map<String, Object> data = cardData.data();
						String password = getPassword(data);
						String passwordHash = Crypto.digest(salt, password);
						final String email = getEmail(cardData.data());
						String moniker  = getMoniker(cardData.data());
						strategy.signup(email, moniker, salt, passwordHash, callback);
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
						boolean monikerOk = Strings.nullSafeToString(getMoniker(data)).length() > 0;
						boolean passwordOk = getPassword(data).length() > 0 && getPassword(data).equals(getConfirmPassword(data));
						return emailOk && monikerOk && passwordOk;
					}

					private String getEmail(Map<String, Object> data) {
						return get(data, LoginConstants.emailKey);
					}

					private String getMoniker(Map<String, Object> data) {
						return get(data, LoginConstants.monikerKey);
					}

					private String getPassword(Map<String, Object> data) {
						return get(data, LoginConstants.passwordKey);
					}

					private String getConfirmPassword(Map<String, Object> data) {
						return get(data, LoginConstants.confirmPasswordKey);
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

}
