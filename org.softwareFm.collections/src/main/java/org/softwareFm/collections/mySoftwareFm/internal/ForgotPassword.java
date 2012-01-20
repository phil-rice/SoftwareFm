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
import org.softwareFm.collections.mySoftwareFm.IForgotPassword;
import org.softwareFm.collections.mySoftwareFm.IForgotPasswordCallback;
import org.softwareFm.collections.mySoftwareFm.ILoginCallbacks;
import org.softwareFm.collections.mySoftwareFm.ILoginDisplayStrategy;
import org.softwareFm.collections.mySoftwareFm.ILoginStrategy;
import org.softwareFm.collections.mySoftwareFm.IShowMessage;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class ForgotPassword implements IForgotPassword {
	private final String cardType = CardConstants.forgotPasswordCardType;
	private final INamesAndValuesEditor content;

	public ForgotPassword(Composite parent, final CardConfig cardConfig, final String sessionSalt, final ILoginStrategy loginStrategy, final ILoginDisplayStrategy loginDisplayStrategy, final IForgotPasswordCallback forgotPasswordCallback) {
		String title = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, cardType, CardConstants.forgotPasswordTitle);
		content = INamesAndValuesEditor.Utils.editor(parent, cardConfig, cardType, title, "", Maps.stringObjectLinkedMap(), Arrays.asList(//
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
						loginDisplayStrategy.showLogin(sessionSalt);
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
		Swts.Buttons.makePushButtonAtStart(buttonComposite, CardConfig.resourceGetter(content), CardConstants.signUpButtonTitle, new Runnable() {
			@Override
			public void run() {
				loginDisplayStrategy.showSignup(sessionSalt);
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
		Swts.Show.display(ForgotPassword.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite parent) throws Exception {
				CardConfig cardConfig = ICardConfigurator.Utils.cardConfigForTests(parent.getDisplay());
				return (Composite) new ForgotPassword(parent, cardConfig, //
						UUID.randomUUID().toString(), //
						ILoginStrategy.Utils.sysoutLoginStrategy(), //
						ILoginDisplayStrategy.Utils.sysoutDisplayStrategy(),//
						ILoginCallbacks.Utils.showMessageCallbacks(cardConfig, IShowMessage.Utils.sysout())).getControl();
			}
		});
	}

}
