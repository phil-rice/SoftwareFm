package org.softwareFm.collections.explorer.internal;

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
import org.softwareFm.utilities.runnable.Runnables;

public class MySoftwareFmLoggedIn implements IHasControl {

	private final TextInBorderWithButtons content;

	public MySoftwareFmLoggedIn(Composite parent, CardConfig cardConfig, String title, String text, final String email, final ILoginDisplayStrategy loginDisplayStrategy, IChangePasswordCallback changePasswordCallback) {
		content = new TextInBorderWithButtons(parent, SWT.WRAP | SWT.READ_ONLY, cardConfig);
		content.setText(CardConstants.loginCardType, title, text);
		content.addButton("Logout", Runnables.sysout("Logout clicked"));
		content.addButton("Change Password", new Runnable() {
			@Override
			public void run() {
				loginDisplayStrategy.showChangePassword(email);
			}
		});
	}

	@Override
	public Control getControl() {
		return content.getControl();
	}

	public static void main(String[] args) {
		Swts.Show.display(MySoftwareFmLoggedIn.class.getSimpleName(),new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				CardConfig cardConfig = ICardConfigurator.Utils.cardConfigForTests(from.getDisplay()) ;
				return (Composite) new MySoftwareFmLoggedIn(from,  cardConfig, "title", "text", "email", ILoginDisplayStrategy.Utils.sysoutDisplayStrategy(), ILoginCallbacks.Utils.showMessageCallbacks(cardConfig, IShowMessage.Utils.sysout())).getControl();
			}
		});
	}
}
