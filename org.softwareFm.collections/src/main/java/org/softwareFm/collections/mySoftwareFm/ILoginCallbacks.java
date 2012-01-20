package org.softwareFm.collections.mySoftwareFm;

import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.utilities.resources.IResourceGetter;

public interface ILoginCallbacks extends ILoginCallback, IForgotPasswordCallback, ISignUpCallback {

	public static class Utils {
		public static ILoginCallbacks showMessageCallbacks(final CardConfig cardConfig, final IShowMessage strategy) {
			return new ILoginCallbacks() {
				@Override
				public void emailSent(String email, String magicString) {
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
				public void loggedIn(String email, String cryptoKey) {
					String cardType = CardConstants.loginCardType;
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
				public void signedUp(String email) {
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

			};
		}
	}
}
