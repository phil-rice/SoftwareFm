package org.arc4eclipse.displayJavadocAndSource;

import org.arc4eclipse.displayCore.api.AbstractDisplayerWithLabel;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.DisplayerDetails;
import org.arc4eclipse.displayCore.api.IRegisteredItems;
import org.arc4eclipse.utilities.strings.Strings;
import org.eclipse.swt.widgets.Composite;

public class SourceDisplayer extends AbstractDisplayerWithLabel<SourcePanel> {

	@Override
	public SourcePanel createLargeControl(DisplayerContext context, IRegisteredItems registeredItems, Composite parent, DisplayerDetails displayerDetails) {
		return new SourcePanel(parent, context, displayerDetails);
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, SourcePanel largeControl, Object value) {
		largeControl.setValue(bindingContext.url, getBindingRipperResult(bindingContext), Strings.nullSafeToString(value));
	}
}