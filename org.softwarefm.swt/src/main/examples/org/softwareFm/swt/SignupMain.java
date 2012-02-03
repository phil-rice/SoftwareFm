package org.softwareFm.swt;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.configuration.ICardConfigurator;
import org.softwareFm.swt.dataStore.ICardDataStore;
import org.softwareFm.swt.mySoftwareFm.ILoginCallbacks;
import org.softwareFm.swt.mySoftwareFm.ILoginDisplayStrategy;
import org.softwareFm.swt.mySoftwareFm.ILoginStrategy;
import org.softwareFm.swt.mySoftwareFm.IShowMessage;
import org.softwareFm.swt.mySoftwareFm.internal.Signup;
import org.softwareFm.swt.swt.Swts;

public class SignupMain {
	public static void main(String[] args) {
		Swts.Show.display(Signup.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite parent) throws Exception {
				CardConfig cardConfig = ICardConfigurator.Utils.basicConfigurator().configure(parent.getDisplay(), new CardConfig(ICardFactory.Utils.noCardFactory(), ICardDataStore.Utils.mock()));
				String salt = "someSalt";
				return (Composite) new Signup(parent, cardConfig, salt, "initialEmail", ILoginStrategy.Utils.sysoutLoginStrategy(), //
						ILoginDisplayStrategy.Utils.sysoutDisplayStrategy(), //
						ILoginCallbacks.Utils.showMessageCallbacks(cardConfig, IShowMessage.Utils.sysout())).getControl();
			}
		});
	}
}

