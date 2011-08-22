package org.arc4eclipse.displayCore.api;

import org.arc4eclipse.displayCore.api.impl.ITopButtonState;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface IDisplayer<L extends Control, S extends Control> {

	S createSmallControl(DisplayerContext displayerContext, ITopButtonState topButtonState, Composite parent, DisplayerDetails displayerDetails);

	L createLargeControl(DisplayerContext context, Composite parent, DisplayerDetails displayerDetails);

	void populateSmallControl(BindingContext bindingContext, S smallControl, Object value);

	void populateLargeControl(BindingContext bindingContext, L largeControl, Object value);

}
