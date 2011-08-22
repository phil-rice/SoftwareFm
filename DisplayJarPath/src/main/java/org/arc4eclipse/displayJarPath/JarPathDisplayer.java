package org.arc4eclipse.displayJarPath;

import org.arc4eclipse.displayCore.api.AbstractDisplayerWithLabel;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.DisplayerDetails;
import org.arc4eclipse.displayCore.api.IRegisteredItems;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class JarPathDisplayer extends AbstractDisplayerWithLabel<DisplayJarPanel> {

	@Override
	public DisplayJarPanel createLargeControl(DisplayerContext context, IRegisteredItems registeredItems, Composite parent, DisplayerDetails displayerDetails) {
		return new DisplayJarPanel(parent, SWT.BORDER, context);
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, DisplayJarPanel largeControl, Object value) {
		BindingRipperResult ripped = getBindingRipperResult(bindingContext);
		largeControl.setValue(bindingContext.url, ripped);
	}

}