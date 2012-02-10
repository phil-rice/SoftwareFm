package org.softwareFm.swt.explorer.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.runnable.Runnables;
import org.softwareFm.swt.card.composites.TextInBorderWithButtons;
import org.softwareFm.swt.composites.IHasControl;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.configuration.ICardConfigurator;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.mySoftwareFm.IChangePasswordCallback;
import org.softwareFm.swt.mySoftwareFm.ILoginCallbacks;
import org.softwareFm.swt.mySoftwareFm.ILoginDisplayStrategy;
import org.softwareFm.swt.mySoftwareFm.IShowMessage;
import org.softwareFm.swt.swt.Swts;

public class MySoftwareFmLoggedIn implements IHasControl {

	private final TextInBorderWithButtons content;

	public MySoftwareFmLoggedIn(Composite parent, CardConfig cardConfig, String title, String text, final UserData userData, final ILoginDisplayStrategy loginDisplayStrategy, Runnable logout, IChangePasswordCallback changePasswordCallback, Runnable showMyData, Runnable showMyGroups) {
		final String email = userData.email();
		content = new TextInBorderWithButtons(parent, SWT.WRAP | SWT.READ_ONLY, cardConfig);
		content.setTextFromResourceGetter(CardConstants.loginCardType, title, text, email);
		content.addButton("Logout", logout);
		content.addButton("Change Password", new Runnable() {
			@Override
			public void run() {
				loginDisplayStrategy.showChangePassword(email);
			}
		});
		content.addButton("My Data", showMyData);
		content.addButton("My Groups", showMyGroups);
	}

	@Override
	public Control getControl() {
		return content.getControl();
	}

	public static void main(String[] args) {
		Swts.Show.display(MySoftwareFmLoggedIn.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				CardConfig cardConfig = ICardConfigurator.Utils.cardConfigForTests(from.getDisplay());
				return (Composite) new MySoftwareFmLoggedIn(from, cardConfig, CardConstants.loggedInTitle, CardConstants.loggedInText, //
						new UserData("email", null, null), //
						ILoginDisplayStrategy.Utils.sysoutDisplayStrategy(), Runnables.sysout("Logout clicked"), //
						ILoginCallbacks.Utils.showMessageCallbacks(cardConfig, IShowMessage.Utils.sysout()),//
						Runnables.sysout("show my data"), Runnables.sysout("show my groups")).getControl();
			}
		});
	}
}
