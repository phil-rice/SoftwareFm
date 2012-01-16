package org.softwareFm.collections.mySoftwareFm;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.collections.mySoftwareFm.internal.Login;
import org.softwareFm.display.composites.IHasComposite;

public interface ILogin extends IHasComposite {

	public static class Utils{
		public static ILogin login(Composite parent, CardConfig cardConfig, String salt, ILoginStrategy loginStrategy){
			return new Login(parent, cardConfig, salt, loginStrategy);
		}
	}
}
