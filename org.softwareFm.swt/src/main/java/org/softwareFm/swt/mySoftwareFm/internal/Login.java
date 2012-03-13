/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.mySoftwareFm.internal;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.editors.ICardEditorCallback;
import org.softwareFm.swt.editors.INamesAndValuesEditor;
import org.softwareFm.swt.mySoftwareFm.ILogin;
import org.softwareFm.swt.mySoftwareFm.ILoginCallback;
import org.softwareFm.swt.mySoftwareFm.ILoginDisplayStrategy;
import org.softwareFm.swt.mySoftwareFm.ILoginStrategy;
import org.softwareFm.swt.mySoftwareFm.IRequestSaltCallback;
import org.softwareFm.swt.swt.Swts;

public class Login implements ILogin {
	private final String cardType = CardConstants.loginCardType;
	final INamesAndValuesEditor content;

	public Login(Composite parent, final CardConfig cardConfig, final String sessionSalt, String initialEmail, final ILoginStrategy loginStrategy, final ILoginDisplayStrategy loginDisplayStrategy, final ILoginCallback callback) {
		IResourceGetter resourceGetter = Functions.call(cardConfig.resourceGetterFn, cardType);
		String title = IResourceGetter.Utils.getOrException(resourceGetter,  CardConstants.loginTitle);
		content = INamesAndValuesEditor.Utils.editor(parent, cardConfig, cardType, title, "", Maps.stringObjectLinkedMap(LoginConstants.emailKey, initialEmail), Arrays.asList(//
				INamesAndValuesEditor.Utils.text(cardConfig, "email"),//
				INamesAndValuesEditor.Utils.password(cardConfig, "password")),//
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
		Swts.Buttons.makePushButtonAtStart(buttonComposite, resourceGetter, CardConstants.signUpButtonTitle, new Runnable() {
			@Override
			public void run() {
				loginDisplayStrategy.showSignup(sessionSalt, getEmail());
			}
		});
		Swts.Buttons.makePushButtonAtStart(buttonComposite, resourceGetter, CardConstants.forgotPasswordButtonTitle, new Runnable() {
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



}