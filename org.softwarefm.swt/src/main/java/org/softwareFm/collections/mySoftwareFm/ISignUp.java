package org.softwareFm.collections.mySoftwareFm;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.collections.mySoftwareFm.internal.Signup;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.swt.configuration.CardConfig;

public interface ISignUp extends IHasComposite {

	public static class Utils {
		public static ISignUp signUp(Composite parent, CardConfig cardConfig, String salt, String initialEmail, ILoginStrategy strategy, ILoginDisplayStrategy loginDisplayStrategy, ISignUpCallback callback) {
			return new Signup(parent, cardConfig, salt, initialEmail, strategy, loginDisplayStrategy, callback);
		}
	}

}
