package org.softwareFm.swt.mySoftwareFm;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.mySoftwareFm.internal.ChangePassword;

public interface IChangePassword extends IHasComposite {

	public static class Utils{
		public static IChangePassword changePassword(Composite parent, CardConfig cardConfig, String initialEmail,ILoginStrategy loginStrategy, ILoginDisplayStrategy loginDisplayStrategy,IChangePasswordCallback callback){
			return new ChangePassword(parent, cardConfig, initialEmail, loginStrategy, loginDisplayStrategy, callback);
		}
	}
}
