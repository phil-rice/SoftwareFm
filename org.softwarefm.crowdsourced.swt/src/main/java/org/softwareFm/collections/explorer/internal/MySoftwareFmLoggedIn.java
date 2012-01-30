package org.softwareFm.collections.explorer.internal;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.card.composites.TextInBorderWithButtons;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.configuration.ICardConfigurator;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.collections.mySoftwareFm.IChangePasswordCallback;
import org.softwareFm.collections.mySoftwareFm.ILoginCallbacks;
import org.softwareFm.collections.mySoftwareFm.ILoginDisplayStrategy;
import org.softwareFm.collections.mySoftwareFm.IShowMessage;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.runnable.Runnables;

public class MySoftwareFmLoggedIn implements IHasControl {

	private final TextInBorderWithButtons content;

	public MySoftwareFmLoggedIn(Composite parent, CardConfig cardConfig, String title, String text, final UserData userData, final ILoginDisplayStrategy loginDisplayStrategy, Runnable logout, IChangePasswordCallback changePasswordCallback) {
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
		content.addButton("My Data", new Runnable() {
			@Override
			public void run() {
				Map<String, Object> map = Maps.stringObjectMap("data", "goes here");
				content.setText(CardConstants.loginCardType, "Project Data", map.toString());
			}
		});
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
						ILoginCallbacks.Utils.showMessageCallbacks(cardConfig, IShowMessage.Utils.sysout())).getControl();
			}
		});
	}
}
