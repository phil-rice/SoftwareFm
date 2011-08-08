package org.arc4eclipse.panel;

import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.eclipse.jdt.core.dom.ITypeBinding;

public interface ISelectedBindingListener {

	void selectionOccured(ITypeBinding binding, BindingRipperResult ripperResult);

}
