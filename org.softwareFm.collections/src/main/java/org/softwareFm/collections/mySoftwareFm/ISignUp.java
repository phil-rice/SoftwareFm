package org.softwareFm.collections.mySoftwareFm;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.collections.mySoftwareFm.internal.Signup;
import org.softwareFm.display.composites.IHasComposite;

public interface ISignUp extends IHasComposite{
	
	public static class Utils{
		public static ISignUp signUp(Composite parent, CardConfig cardConfig, String salt, String cryptoKey, ILoginStrategy strategy){
			return new Signup(parent, cardConfig, salt, cryptoKey, strategy);
		}
	}

}
