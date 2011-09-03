package org.softwareFm.panel;

import org.eclipse.jdt.core.dom.ITypeBinding;
import org.softwareFm.jdtBinding.api.BindingRipperResult;

public interface ISelectedBindingListener {

	void selectionOccured(ITypeBinding binding, BindingRipperResult ripperResult);

}
