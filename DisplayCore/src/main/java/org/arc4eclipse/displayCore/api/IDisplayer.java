package org.arc4eclipse.displayCore.api;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface IDisplayer {

	String getNameSpace();

	Control makeCompositeAsChildOf(Composite parent, BindingContext bindingContext, String url, Map<String, Object> data, NameSpaceNameAndValue nameSpaceNameAndValue);

}
