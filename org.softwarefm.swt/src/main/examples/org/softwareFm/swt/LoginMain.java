package org.softwareFm.swt;

import java.util.UUID;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.configuration.ICardConfigurator;
import org.softwareFm.swt.mySoftwareFm.ILoginCallbacks;
import org.softwareFm.swt.mySoftwareFm.ILoginDisplayStrategy;
import org.softwareFm.swt.mySoftwareFm.ILoginStrategy;
import org.softwareFm.swt.mySoftwareFm.IShowMessage;
import org.softwareFm.swt.mySoftwareFm.internal.Login;
import org.softwareFm.swt.swt.Swts;

public class LoginMain {
	public static void main(String[] args) {
		Swts.Show.display(Login.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite parent) throws Exception {
				CardConfig cardConfig = ICardConfigurator.Utils.cardConfigForTests(parent.getDisplay());
				String salt = UUID.randomUUID().toString();
				ILoginStrategy sysoutLoginStrategy = ILoginStrategy.Utils.sysoutLoginStrategy();
				ILoginDisplayStrategy loginDisplayStrategy = ILoginDisplayStrategy.Utils.sysoutDisplayStrategy();
				ILoginCallbacks loginCallbacks = ILoginCallbacks.Utils.showMessageCallbacks(cardConfig, IShowMessage.Utils.sysout());
				return (Composite) new Login(parent, cardConfig, salt, "initialEmail", sysoutLoginStrategy, loginDisplayStrategy, loginCallbacks).getControl();
			}
		});
	}
}