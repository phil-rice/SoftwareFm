/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.explorer.internal;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.crowdsource.api.ICrowdSourceReadWriteApi;
import org.softwareFm.crowdsource.utilities.arrays.ArrayHelper;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.jarAndClassPath.api.IUserDataListener;
import org.softwareFm.jarAndClassPath.api.IUserDataManager;
import org.softwareFm.jarAndClassPath.api.UserData;
import org.softwareFm.swt.card.composites.TextInBorder;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.explorer.IShowMyData;
import org.softwareFm.swt.explorer.IShowMyGroups;
import org.softwareFm.swt.mySoftwareFm.IChangePassword;
import org.softwareFm.swt.mySoftwareFm.IChangePasswordCallback;
import org.softwareFm.swt.mySoftwareFm.IForgotPassword;
import org.softwareFm.swt.mySoftwareFm.IForgotPasswordCallback;
import org.softwareFm.swt.mySoftwareFm.ILogin;
import org.softwareFm.swt.mySoftwareFm.ILoginCallback;
import org.softwareFm.swt.mySoftwareFm.ILoginDisplayStrategy;
import org.softwareFm.swt.mySoftwareFm.ILoginStrategy;
import org.softwareFm.swt.mySoftwareFm.IRequestSaltCallback;
import org.softwareFm.swt.mySoftwareFm.ISignUp;
import org.softwareFm.swt.mySoftwareFm.ISignUpCallback;
import org.softwareFm.swt.swt.Swts;

public class MySoftwareFm implements IHasComposite, ILoginDisplayStrategy {

	private final Composite content;
	private final ILoginStrategy loginStrategy;
	private final CardConfig cardConfig;
	protected String signupSalt;

	protected ICallback<String> restart;
	private final IShowMyData showMyData;
	private final IShowMyGroups showMyGroups;
	private final IUserDataManager userDataManager;
	private final ICrowdSourceReadWriteApi readWriteApi;

	public MySoftwareFm(Composite parent,ICrowdSourceReadWriteApi readWriteApi,  CardConfig cardConfig, ILoginStrategy loginStrategy, IShowMyData showMyData, IShowMyGroups showMyGroups, IUserDataManager userDataManager) {
		this.readWriteApi = readWriteApi;
		this.cardConfig = cardConfig;
		this.loginStrategy = loginStrategy;
		this.showMyData = showMyData;
		this.showMyGroups = showMyGroups;
		this.userDataManager = userDataManager;
		this.content = new Composite(parent, SWT.NULL);
		content.setLayout(new FillLayout());
		restart = new ICallback<String>() {
			@Override
			public void process(String email) throws Exception {
				logout();
				start();
			}
		};
		userDataManager.addUserDataListener(new IUserDataListener() {
			@Override
			public void userDataChanged(Object source, UserData userData) {
				if (source != MySoftwareFm.this)
					start();
			}
		});
	}

	public void start() {
		UserData userData = userDataManager.getUserData();
		final String email = userData.email();
		if (userData.isLoggedIn())
			showLoggedIn();
		else {
			displayWithClickBackToStart(CardConstants.loginCardType, CardConstants.contactingServerTitle, CardConstants.contactingServerText, email);
			loginStrategy.requestSessionSalt(new IRequestSaltCallback() {
				@Override
				public void saltReceived(String salt) {
					MySoftwareFm.this.signupSalt = salt;
					showLogin(salt, email);
				}

				@Override
				public void problemGettingSalt(String message) {
					displayWithClickBackToStart(CardConstants.loginCardType, CardConstants.failedToContactServerTitle, CardConstants.failedToContactServerText, email, message);
				}
			});
		}
	}

	@SuppressWarnings("unused")
	public void showLoggedIn() {
		final UserData userData = userDataManager.getUserData();
		final String email = userData.email();
		Swts.removeAllChildren(content);
		IChangePasswordCallback callback = null;
		new MySoftwareFmLoggedIn(content, readWriteApi, cardConfig, CardConstants.loggedInTitle, CardConstants.loggedInText, userData, this, new IMySoftwareFmLoggedInStrategy() {

			@Override
			public void showMyGroups() {
				showMyGroups.show(userData, null);
			}

			@Override
			public void showMyData() {
				showMyData.show(userData);
			}

			@Override
			public void logout() {
				MySoftwareFm.this.logout();
				start();
			}

			@Override
			public List<String> displayProperties() {
				return CardConstants.mySoftwareFmDisplayProperties;
			}
		});
		content.layout();
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	public void display(final String cardType, final String title, final String text, final String... args) {
		content.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				Swts.removeAllChildren(content);
				TextInBorder textInBorder = new TextInBorder(content, SWT.WRAP | SWT.READ_ONLY, cardConfig);
				textInBorder.setTextFromResourceGetter(cardType, title, text, (Object[]) args);
				content.layout();
			}
		});
	}

	public void displayWithClickBackToStart(final String cardType, final String title, final String text, final String email, final String... args) {
		displayWithClick(cardType, title, text, email, new Runnable() {
			@Override
			public void run() {
				ICallback.Utils.call(restart, email);
			}
		}, ArrayHelper.insert(args, email));
	}

	public void displayWithClick(final String cardType, final String title, final String text, final String email, final Runnable runnable, final String... args) {
		content.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				Swts.removeAllChildren(content);
				TextInBorder textInBorder = new TextInBorder(content, SWT.WRAP | SWT.READ_ONLY, cardConfig);
				textInBorder.setTextFromResourceGetter(cardType, title, text, (Object[]) args);
				textInBorder.addClickedListener(runnable);
				content.layout();
			}
		});
	}

	@Override
	public void showLogin(String sessionSalt, String email) {
		Swts.removeAllChildren(content);
		ILogin.Utils.login(content, cardConfig, sessionSalt, email, loginStrategy, MySoftwareFm.this, new ILoginCallback() {
			@Override
			public void loggedIn(UserData userData) {
				setUserDataInManager(userData);
				showLoggedIn();
			}

			@Override
			public void failedToLogin(String email, String message) {
				setUserDataManagerWithJustEmail(email);
				displayWithClickBackToStart(CardConstants.loginCardType, CardConstants.failedToLoginTitle, CardConstants.failedToLoginText, email, message);
			}
		});
		content.layout();
	}

	@Override
	public void showForgotPassword(String sessionSalt, String initialEmail) {
		Swts.removeAllChildren(content);
		IForgotPassword.Utils.forgotPassword(content, cardConfig, sessionSalt, initialEmail, loginStrategy, this, new IForgotPasswordCallback() {
			@Override
			public void emailSent(String email) {
				setUserDataManagerWithJustEmail(email);
				displayWithClickBackToStart(CardConstants.forgotPasswordCardType, CardConstants.sentForgottenPasswordTitle, CardConstants.sentForgottenPasswordText, email);
			}

			@Override
			public void failedToSend(String email, String message) {
				setUserDataManagerWithJustEmail(email);
				displayWithClickBackToStart(CardConstants.forgotPasswordCardType, CardConstants.failedToSendForgottenPasswordTitle, CardConstants.failedToSendForgottenPasswordText, email, message);
			}
		});
		content.layout();
	}

	@Override
	public void showSignup(String sessionSalt, String initialEmail) {
		Swts.removeAllChildren(content);
		ISignUp.Utils.signUp(content, cardConfig, sessionSalt, initialEmail, loginStrategy, this, new ISignUpCallback() {
			@Override
			public void signedUp(UserData userData) {
				setUserDataInManager(userData);
				showLoggedIn();
			}

			@Override
			public void failed(String email, String message) {
				setUserDataManagerWithJustEmail(email);
				displayWithClickBackToStart(CardConstants.signupCardType, CardConstants.failedToSignupTitle, CardConstants.failedToSignupText, email, message);
			}
		});
		content.layout();
	}

	@Override
	public void showChangePassword(final String email) {
		Swts.removeAllChildren(content);
		final String cardType = CardConstants.changePasswordCardType;
		final Runnable showLoggedIn = new Runnable() {
			@Override
			public void run() {
				showLoggedIn();
			}
		};
		IChangePassword.Utils.changePassword(content, cardConfig, email, loginStrategy, this, new IChangePasswordCallback() {
			@Override
			public void changedPassword(String email) {
				displayWithClick(cardType, CardConstants.changedPasswordTitle, CardConstants.changedPasswordText, email, showLoggedIn);
			}

			@Override
			public void failedToChangePassword(String email, String message) {
				displayWithClick(cardType, CardConstants.failedToChangePasswordTitle, CardConstants.failedToChangePasswordText, email, showLoggedIn, message);
			}

			@Override
			public void cancelChangePassword() {
				showLoggedIn();
			}
		});
		content.layout();

	}

	public String getSignupSalt() {
		return signupSalt;
	}

	public void logout() {
		userDataManager.setUserData(this, userDataManager.getUserData().loggedOut());
		signupSalt = null;
	}

	public void forceFocus() {
		Control[] children = content.getChildren();
		if (children.length > 0)
			children[0].forceFocus();

	}

	public UserData getUserData() {
		return userDataManager.getUserData();
	}

	protected void setUserDataInManager(UserData userData) {
		userDataManager.setUserData(this, userData);
	}

	protected void setUserDataManagerWithJustEmail(String email) {
		userDataManager.setUserData(this, new UserData(email, null, null));
	}

}