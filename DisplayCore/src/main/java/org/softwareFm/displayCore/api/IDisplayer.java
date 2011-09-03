package org.softwareFm.displayCore.api;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface IDisplayer<L extends Control, S extends Control> {

	S createSmallControl(DisplayerContext displayerContext, IRegisteredItems registeredItems, ITopButtonState topButtonState, Composite parent, DisplayerDetails displayerDetails);

	L createLargeControl(DisplayerContext context, IRegisteredItems registeredItems, Composite parent, DisplayerDetails displayerDetails);

	void populateSmallControl(BindingContext bindingContext, S smallControl, Object value);

	void populateLargeControl(BindingContext bindingContext, L largeControl, Object value);

}
