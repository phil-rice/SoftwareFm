package org.softwareFm.swt.mySoftwareFm;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.mySoftwareFm.internal.Signup;

public interface ISignUp extends IHasComposite {

	public static class Utils {
		public static ISignUp signUp(Composite parent, CardConfig cardConfig, String salt, String initialEmail, ILoginStrategy strategy, ILoginDisplayStrategy loginDisplayStrategy, ISignUpCallback callback) {
			return new Signup(parent, cardConfig, salt, initialEmail, strategy, loginDisplayStrategy, callback);
		}
	}

}
