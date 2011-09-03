package org.softwareFm.displayJarPath;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.displayCore.api.AbstractDisplayerWithLabel;
import org.softwareFm.displayCore.api.BindingContext;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.displayCore.api.IRegisteredItems;
import org.softwareFm.jdtBinding.api.BindingRipperResult;

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