package org.softwareFm.displayJarPath;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.displayCore.api.AbstractDisplayerWithLabel;
import org.softwareFm.displayCore.api.BindingContext;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.displayCore.api.IRegisteredItems;
import org.softwareFm.jdtBinding.api.BindingRipperResult;

public class JarSummaryPathDisplayer extends AbstractDisplayerWithLabel<DisplayJarSummaryPanel> {

	@Override
	public DisplayJarSummaryPanel createLargeControl(DisplayerContext context, IRegisteredItems registeredItems, Composite parent, DisplayerDetails displayerDetails) {
		return new DisplayJarSummaryPanel(parent, SWT.BORDER, context, displayerDetails);
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, DisplayJarSummaryPanel largeControl, Object value) {
		BindingRipperResult ripped = getBindingRipperResult(bindingContext);
		largeControl.setValue(bindingContext.url, ripped, bindingContext.data);
	}

}