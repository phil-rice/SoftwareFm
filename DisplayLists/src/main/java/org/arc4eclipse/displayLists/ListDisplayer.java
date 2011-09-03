package org.arc4eclipse.displayLists;

import org.arc4eclipse.displayCore.api.AbstractDisplayerWithLabel;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.DisplayerDetails;
import org.arc4eclipse.displayCore.api.IRegisteredItems;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ListDisplayer extends AbstractDisplayerWithLabel<ListPanel<?>> {

	@Override
	public ListPanel<?> createLargeControl(DisplayerContext context, IRegisteredItems registeredItems, Composite parent, DisplayerDetails displayerDetails) {
		return new ListPanel<Object>(parent, SWT.BORDER, context, displayerDetails, registeredItems);
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, ListPanel<?> largeControl, Object value) {
		largeControl.setValue(bindingContext.url, value);
	}

}