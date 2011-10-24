package org.softwareFm.jdtBinding.api;

import java.util.Map;

import org.eclipse.jdt.core.dom.IBinding;
import org.softwareFm.jdtBinding.api.impl.BindingRipper;

public interface IBindingRipper {

	BindingRipperResult rip(IBinding binding, Map<String, Object> cargo);

	public static class Utils {
		public static IBindingRipper ripper() {
			return new BindingRipper();
		}
	}

}
