package org.softwareFm.collections.mySoftwareFm.internal;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.card.ICardData;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.configuration.ICardConfigurator;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.editors.ICardEditorCallback;
import org.softwareFm.card.editors.INamesAndValuesEditor;
import org.softwareFm.collections.mySoftwareFm.IChangePassword;
import org.softwareFm.collections.mySoftwareFm.IChangePasswordCallback;
import org.softwareFm.collections.mySoftwareFm.ILoginCallbacks;
import org.softwareFm.collections.mySoftwareFm.ILoginDisplayStrategy;
import org.softwareFm.collections.mySoftwareFm.ILoginStrategy;
import org.softwareFm.collections.mySoftwareFm.IRequestSaltCallback;
import org.softwareFm.collections.mySoftwareFm.IShowMessage;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.display.swt.Swts;

public class ChangePassword implements IChangePassword {
	private final String cardType = CardConstants.changePasswordCardType;
	final INamesAndValuesEditor content;
	private final String email;

	public ChangePassword(Composite parent, final CardConfig cardConfig, String initialEmail, final ILoginStrategy loginStrategy, final ILoginDisplayStrategy loginDisplayStrategy, final IChangePasswordCallback callback) {
		this.email = initialEmail;
		String title = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, cardType, CardConstants.changePasswordTitle);
		content = INamesAndValuesEditor.Utils.editor(parent, cardConfig, cardType, title, "", Maps.stringObjectLinkedMap(LoginConstants.emailKey, initialEmail), Arrays.asList(//
				INamesAndValuesEditor.Utils.password(cardConfig, cardType, "password"),//
				INamesAndValuesEditor.Utils.password(cardConfig, cardType, "newPassword"),//
				INamesAndValuesEditor.Utils.password(cardConfig, cardType, "confirmNewPassword")),//
				new ICardEditorCallback() {
					@Override
					public void ok(ICardData cardData) {
						final Map<String, Object> data = cardData.data();
						final String oldPassword = getPassword(data);
						final String newPassword = getNewPassword(data);
						final String confirmPassword = getConfirmNewPassword(cardData.data());
						assert newPassword.equals(confirmPassword) : "Old: " + oldPassword + " Confirm: " + confirmPassword;
						loginStrategy.requestEmailSalt(email, "", new IRequestSaltCallback() {
							@Override
							public void saltReceived(String salt) {
								String oldHash = Crypto.digest(salt, oldPassword);
								String newHash = Crypto.digest(salt, newPassword);
								loginStrategy.changePassword(email, oldHash, newHash, callback);
							}
							
							@Override
							public void problemGettingSalt(String message) {
								callback.failedToChangePassword(email, message);
							}
						});

					}

					private String get(Map<String, Object> data, String key) {
						return Strings.nullSafeToString(data.get(key));
					}

					@Override
					public void cancel(ICardData cardData) {
						callback.cancelChangePassword();
					}

					@Override
					public boolean canOk(Map<String, Object> data) {
						boolean passwordOk = getPassword(data).length() > 0;
						String newPassword = getNewPassword(data);
						boolean newPasswordOk = newPassword.length() > 0 && getConfirmNewPassword(data).equals(newPassword);
						return passwordOk && newPasswordOk;
					}

					private String getPassword(Map<String, Object> data) {
						return get(data, "password");
					}

					private String getNewPassword(Map<String, Object> data) {
						return get(data, "newPassword");
					}

					private String getConfirmNewPassword(Map<String, Object> data) {
						return get(data, "confirmNewPassword");
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
		Swts.Show.display(ChangePassword.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite parent) throws Exception {
				CardConfig cardConfig = ICardConfigurator.Utils.cardConfigForTests(parent.getDisplay());
				ILoginStrategy sysoutLoginStrategy = ILoginStrategy.Utils.sysoutLoginStrategy();
				ILoginDisplayStrategy loginDisplayStrategy = ILoginDisplayStrategy.Utils.sysoutDisplayStrategy();
				ILoginCallbacks loginCallbacks = ILoginCallbacks.Utils.showMessageCallbacks(cardConfig, IShowMessage.Utils.sysout());
				return (Composite) new ChangePassword(parent, cardConfig, "initialEmail", sysoutLoginStrategy, loginDisplayStrategy, loginCallbacks).getControl();
			}
		});
	}

}
