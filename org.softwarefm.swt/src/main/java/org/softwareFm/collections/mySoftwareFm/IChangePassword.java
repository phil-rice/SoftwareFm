package org.softwareFm.collections.mySoftwareFm;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.collections.mySoftwareFm.internal.ChangePassword;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.swt.configuration.CardConfig;

public interface IChangePassword extends IHasComposite {

	public static class Utils{
		public static IChangePassword changePassword(Composite parent, CardConfig cardConfig, String initialEmail,ILoginStrategy loginStrategy, ILoginDisplayStrategy loginDisplayStrategy,IChangePasswordCallback callback){
			return new ChangePassword(parent, cardConfig, initialEmail, loginStrategy, loginDisplayStrategy, callback);
		}
	}
}
