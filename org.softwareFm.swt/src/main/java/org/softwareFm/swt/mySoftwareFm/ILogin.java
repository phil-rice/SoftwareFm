package org.softwareFm.swt.mySoftwareFm;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.mySoftwareFm.internal.Login;

public interface ILogin extends IHasComposite {

	public static class Utils{
		public static ILogin login(Composite parent, CardConfig cardConfig, String salt,String initialEmail, ILoginStrategy loginStrategy,ILoginDisplayStrategy loginDisplayStrategy,  ILoginCallback callback){
			return new Login(parent, cardConfig, salt, initialEmail, loginStrategy, loginDisplayStrategy, callback);
		}
	}
}
