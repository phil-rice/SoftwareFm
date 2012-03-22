/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.mySoftwareFm;

import org.softwareFm.crowdsource.utilities.resources.IResourceGetter;
import org.softwareFm.jarAndClassPath.api.UserData;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;

public interface ILoginCallbacks extends ILoginCallback, IForgotPasswordCallback, ISignUpCallback, IChangePasswordCallback {

	abstract public static class Utils {
		public static ILoginCallbacks showMessageCallbacks(final CardConfig cardConfig, final IShowMessage strategy) {
			return new ILoginCallbacks() {
				@Override
				public void emailSent(String email) {
					String cardType = CardConstants.forgotPasswordCardType;
					String title = IResourceGetter.Utils.getMessageOrException(cardConfig.resourceGetterFn, cardType, CardConstants.sentForgottenPasswordTitle, email);
					String message = IResourceGetter.Utils.getMessageOrException(cardConfig.resourceGetterFn, cardType, CardConstants.sentForgottenPasswordText, email);
					strategy.showMessage(cardType, title, message);
				}

				@Override
				public void failedToSend(String email, String errorMessage) {
					String cardType = CardConstants.forgotPasswordCardType;
					String title = IResourceGetter.Utils.getMessageOrException(cardConfig.resourceGetterFn, cardType, CardConstants.failedToSendForgottenPasswordTitle, email, errorMessage);
					String message = IResourceGetter.Utils.getMessageOrException(cardConfig.resourceGetterFn, CardConstants.forgotPasswordCardType, CardConstants.failedToSendForgottenPasswordText, email, errorMessage);
					strategy.showMessage(cardType, title, message);

				}

				@Override
				public void loggedIn(UserData userData) {
					String cardType = CardConstants.loginCardType;
					String email = userData.email();
					String title = IResourceGetter.Utils.getMessageOrException(cardConfig.resourceGetterFn, cardType, CardConstants.loggedInTitle, email);
					String message = IResourceGetter.Utils.getMessageOrException(cardConfig.resourceGetterFn, CardConstants.loginCardType, CardConstants.loggedInText, email);
					strategy.showMessage(cardType, title, message);
				}

				@Override
				public void failedToLogin(String email, String errorMessage) {
					String cardType = CardConstants.loginCardType;
					String title = IResourceGetter.Utils.getMessageOrException(cardConfig.resourceGetterFn, cardType, CardConstants.failedToLoginTitle, email, errorMessage);
					String message = IResourceGetter.Utils.getMessageOrException(cardConfig.resourceGetterFn, cardType, CardConstants.failedToLoginText, email, errorMessage);
					strategy.showMessage(cardType, title, message);
				}

				@Override
				public void signedUp(UserData userData) {
					String email = userData.email();
					String cardType = CardConstants.signupCardType;
					String title = IResourceGetter.Utils.getMessageOrException(cardConfig.resourceGetterFn, cardType, CardConstants.signedUpInTitle, email);
					String message = IResourceGetter.Utils.getMessageOrException(cardConfig.resourceGetterFn, cardType, CardConstants.signedUpText, email);
					strategy.showMessage(cardType, title, message);
				}

				@Override
				public void failed(String email, String errorMessage) {
					String cardType = CardConstants.signupCardType;
					String title = IResourceGetter.Utils.getMessageOrException(cardConfig.resourceGetterFn, cardType, CardConstants.failedToSignupTitle, email, errorMessage);
					String message = IResourceGetter.Utils.getMessageOrException(cardConfig.resourceGetterFn, cardType, CardConstants.failedToSignupText, email, errorMessage);
					strategy.showMessage(cardType, title, message);
				}

				@Override
				public void cancelChangePassword() {
					throw new UnsupportedOperationException();
				}

				@Override
				public void changedPassword(String email) {
					String cardType = CardConstants.changePasswordCardType;
					String title = IResourceGetter.Utils.getMessageOrException(cardConfig.resourceGetterFn, cardType, CardConstants.changedPasswordTitle, email);
					String message = IResourceGetter.Utils.getMessageOrException(cardConfig.resourceGetterFn, cardType, CardConstants.changedPasswordText, email);
					strategy.showMessage(cardType, title, message);
				}

				@Override
				public void failedToChangePassword(String email, String errorMessage) {
					String cardType = CardConstants.changePasswordCardType;
					String title = IResourceGetter.Utils.getMessageOrException(cardConfig.resourceGetterFn, cardType, CardConstants.failedToChangePasswordTitle, email, errorMessage);
					String message = IResourceGetter.Utils.getMessageOrException(cardConfig.resourceGetterFn, cardType, CardConstants.failedToChangePasswordText, email, errorMessage);
					strategy.showMessage(cardType, title, message);
				}

			};
		}
	}
}