package org.arc4eclipse.displayCore.api;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface IDisplayer<L extends Control, S extends Control> {

	String getNameSpace();

	S createSmallControl(DisplayerContext displayerContext, Composite parent, NameSpaceAndName nameSpaceAndName, String title);

	L createLargeControl(DisplayerContext displayerContext, Composite parent, NameSpaceAndName nameSpaceAndName, String title);

	void populateSmallControl(BindingContext bindingContext, S smallControl, Object value);

	void populateLargeControl(BindingContext bindingContext, L largeControl, Object value);

}
