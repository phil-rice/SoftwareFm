package org.arc4eclipse.jdtBinding.api;

import org.arc4eclipse.jdtBinding.api.impl.BindingRipper;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.eclipse.jdt.core.dom.IBinding;

public interface IBindingRipper extends IFunction1<IBinding, BindingRipperResult> {

	public static class Utils {
		public static IBindingRipper ripper() {
			return new BindingRipper();
		}
	}

}
