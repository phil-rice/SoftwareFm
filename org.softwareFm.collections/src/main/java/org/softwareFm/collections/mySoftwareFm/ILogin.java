package org.softwareFm.collections.mySoftwareFm;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.utilities.functions.IFunction1;

public interface ILogin extends IHasComposite {

	public static class Utils {

		public static IFunction1<Composite, Composite> loginFn(final CardConfig cardConfig, final ILoginStrategy loginStrategy) {
			return new IFunction1<Composite, Composite>() {
				@Override
				public Composite apply(Composite parent) throws Exception {
					return new Login(parent, cardConfig, loginStrategy).getComposite();
				}
			};
		}
	}
}
