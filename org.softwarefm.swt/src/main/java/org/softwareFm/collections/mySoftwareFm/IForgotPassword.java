package org.softwareFm.collections.mySoftwareFm;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.collections.mySoftwareFm.internal.ForgotPassword;
import org.softwareFm.display.composites.IHasComposite;

public interface IForgotPassword extends IHasComposite {
	public static class Utils {
		public static IForgotPassword forgotPassword(Composite parent, CardConfig cardConfig, String salt, String initialEmail, ILoginStrategy loginStrategy, ILoginDisplayStrategy loginDisplayStrategy, IForgotPasswordCallback callback) {
			return new ForgotPassword(parent, cardConfig, salt, initialEmail, loginStrategy, loginDisplayStrategy, callback);
		}
	}
}
