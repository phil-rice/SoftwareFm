package org.softwareFm.jdtBinding.api;

import org.eclipse.jdt.core.dom.IBinding;
import org.softwareFm.jdtBinding.api.impl.BindingRipper;
import org.softwareFm.utilities.functions.IFunction1;

public interface IBindingRipper extends IFunction1<IBinding, BindingRipperResult> {

	public static class Utils {
		public static IBindingRipper ripper() {
			return new BindingRipper();
		}
	}

}
