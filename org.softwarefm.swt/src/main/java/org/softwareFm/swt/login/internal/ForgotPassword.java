/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.login.internal;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.resources.IResourceGetter;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.editors.ICardEditorCallback;
import org.softwareFm.swt.editors.INamesAndValuesEditor;
import org.softwareFm.swt.login.IForgotPassword;
import org.softwareFm.swt.login.IForgotPasswordCallback;
import org.softwareFm.swt.login.ILoginDisplayStrategy;
import org.softwareFm.swt.login.ILoginStrategy;
import org.softwareFm.swt.swt.Swts;

public class ForgotPassword implements IForgotPassword {
	private final String cardType = CardConstants.forgotPasswordCardType;
	private final INamesAndValuesEditor content;

	public ForgotPassword(Composite parent, final CardConfig cardConfig, final String sessionSalt, String initialEmail, final ILoginStrategy loginStrategy, final ILoginDisplayStrategy loginDisplayStrategy, final IForgotPasswordCallback forgotPasswordCallback) {
		IResourceGetter resourceGetter = Functions.call(cardConfig.resourceGetterFn, cardType);
		String title = IResourceGetter.Utils.getOrException(resourceGetter,  CardConstants.forgotPasswordTitle);
		String message = IResourceGetter.Utils.getOrException(resourceGetter, CardConstants.forgotPasswordMessage);
		content = INamesAndValuesEditor.Utils.editor(parent, cardConfig, cardType, title, "", Maps.stringObjectLinkedMap(LoginConstants.emailKey, initialEmail, "message", message), Arrays.asList(//
				INamesAndValuesEditor.Utils.text(cardConfig, LoginConstants.emailKey),//
				INamesAndValuesEditor.Utils.message( cardConfig, "message")//
				),//
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
		Swts.Buttons.makePushButtonAtStart(buttonComposite, resourceGetter, CardConstants.signUpButtonTitle, new Runnable() {
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