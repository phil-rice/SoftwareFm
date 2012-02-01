package org.softwareFm.swt.mySoftwareFm;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.mySoftwareFm.internal.ForgotPassword;

public interface IForgotPassword extends IHasComposite {
	public static class Utils {
		public static IForgotPassword forgotPassword(Composite parent, CardConfig cardConfig, String salt, String initialEmail, ILoginStrategy loginStrategy, ILoginDisplayStrategy loginDisplayStrategy, IForgotPasswordCallback callback) {
			return new ForgotPassword(parent, cardConfig, salt, initialEmail, loginStrategy, loginDisplayStrategy, callback);
		}
	}
}
