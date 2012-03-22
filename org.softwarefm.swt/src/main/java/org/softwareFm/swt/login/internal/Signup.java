/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.login.internal;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.resources.IResourceGetter;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.editors.ICardEditorCallback;
import org.softwareFm.swt.editors.INamesAndValuesEditor;
import org.softwareFm.swt.login.ILoginDisplayStrategy;
import org.softwareFm.swt.login.ILoginStrategy;
import org.softwareFm.swt.login.ISignUp;
import org.softwareFm.swt.login.ISignUpCallback;
import org.softwareFm.swt.swt.Swts;

public class Signup implements ISignUp {
	private final String cardType = CardConstants.signupCardType;
	private final INamesAndValuesEditor content;

	public Signup(Composite parent, final CardConfig cardConfig, final String salt, String initialEmail, final ILoginStrategy strategy, final ILoginDisplayStrategy loginDisplayStrategy, final ISignUpCallback callback) {
		IResourceGetter resourceGetter = Functions.call(cardConfig.resourceGetterFn, cardType);
		String title = IResourceGetter.Utils.getOrException(resourceGetter, CardConstants.signupTitle);
		content = INamesAndValuesEditor.Utils.editor(parent, cardConfig, cardType, title, "", Maps.stringObjectLinkedMap(LoginConstants.emailKey, initialEmail), Arrays.asList(//
				INamesAndValuesEditor.Utils.text(cardConfig, LoginConstants.emailKey),//
				INamesAndValuesEditor.Utils.text(cardConfig, LoginConstants.monikerKey),//
				INamesAndValuesEditor.Utils.password(cardConfig, LoginConstants.passwordKey),//
				INamesAndValuesEditor.Utils.password(cardConfig, LoginConstants.confirmPasswordKey)),//
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
		Swts.Buttons.makePushButtonAtStart(buttonComposite, resourceGetter, CardConstants.forgotPasswordButtonTitle, new Runnable() {
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