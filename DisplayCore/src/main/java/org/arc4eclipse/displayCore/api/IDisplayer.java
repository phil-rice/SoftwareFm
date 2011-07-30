package org.arc4eclipse.displayCore.api;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface IDisplayer {

	String getNameSpace();

	// Control makeSmallCompositeAsChildOf(Composite parent, BindingContext bindingContext, String url, Map<String, Object> data, NameSpaceNameAndValue nameSpaceNameAndValue);

	Control makeCompositeAsChildOf(Composite parent, BindingContext bindingContext, NameSpaceNameAndValue nameSpaceNameAndValue);

}
