/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.mySoftwareFm.internal;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.configuration.ICardConfigurator;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.editors.ICardEditorCallback;
import org.softwareFm.swt.editors.INamesAndValuesEditor;
import org.softwareFm.swt.mySoftwareFm.IChangePassword;
import org.softwareFm.swt.mySoftwareFm.IChangePasswordCallback;
import org.softwareFm.swt.mySoftwareFm.ILoginCallbacks;
import org.softwareFm.swt.mySoftwareFm.ILoginDisplayStrategy;
import org.softwareFm.swt.mySoftwareFm.ILoginStrategy;
import org.softwareFm.swt.mySoftwareFm.IRequestSaltCallback;
import org.softwareFm.swt.mySoftwareFm.IShowMessage;
import org.softwareFm.swt.swt.Swts;

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