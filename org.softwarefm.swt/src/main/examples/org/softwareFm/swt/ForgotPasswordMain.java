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
import org.softwareFm.swt.mySoftwareFm.internal.ForgotPassword;
import org.softwareFm.swt.swt.Swts;

public class ForgotPasswordMain {
	public static void main(String[] args) {
		Swts.Show.display(ForgotPassword.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite parent) throws Exception {
				CardConfig cardConfig = ICardConfigurator.Utils.cardConfigForTests(parent.getDisplay());
				return (Composite) new ForgotPassword(parent, cardConfig, //
						UUID.randomUUID().toString(), //
						"initial email",//
						ILoginStrategy.Utils.sysoutLoginStrategy(), //
						ILoginDisplayStrategy.Utils.sysoutDisplayStrategy(),//
						ILoginCallbacks.Utils.showMessageCallbacks(cardConfig, IShowMessage.Utils.sysout())).getControl();
			}
		});
	}

}
