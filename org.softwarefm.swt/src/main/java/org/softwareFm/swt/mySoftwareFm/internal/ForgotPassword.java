package org.softwareFm.swt.mySoftwareFm.internal;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.editors.ICardEditorCallback;
import org.softwareFm.swt.editors.INamesAndValuesEditor;
import org.softwareFm.swt.mySoftwareFm.IForgotPassword;
import org.softwareFm.swt.mySoftwareFm.IForgotPasswordCallback;
import org.softwareFm.swt.mySoftwareFm.ILoginDisplayStrategy;
import org.softwareFm.swt.mySoftwareFm.ILoginStrategy;
import org.softwareFm.swt.swt.Swts;

public class ForgotPassword implements IForgotPassword {
	private final String cardType = CardConstants.forgotPasswordCardType;
	private final INamesAndValuesEditor content;

	public ForgotPassword(Composite parent, final CardConfig cardConfig, final String sessionSalt, String initialEmail, final ILoginStrategy loginStrategy, final ILoginDisplayStrategy loginDisplayStrategy, final IForgotPasswordCallback forgotPasswordCallback) {
		String title = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, cardType, CardConstants.forgotPasswordTitle);
		content = INamesAndValuesEditor.Utils.editor(parent, cardConfig, cardType, title, "", Maps.stringObjectLinkedMap(LoginConstants.emailKey, initialEmail), Arrays.asList(//
				INamesAndValuesEditor.Utils.text(cardConfig, cardType, "email")),//
				new ICardEditorCallback() {
					@Override
					public void ok(ICardData cardData) {
						final String email = getEmail(cardData.data());
						loginStrategy.forgotPassword(email, sessionSalt, forgotPasswordCallback);
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
						boolean emailOk = getEmail(data).length() > 0;
						return emailOk;
					}

					private String getEmail(Map<String, Object> data) {
						return get(data, "email");
					}

				});
		Composite buttonComposite = content.getButtonComposite();
		Swts.Buttons.makeImageButtonAtStart(buttonComposite, cardConfig.imageFn, CardConstants.signUpImage, new Runnable() {
			@Override
			public void run() {
				loginDisplayStrategy.showSignup(sessionSalt, getEmail());
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
