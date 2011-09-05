package org.softwareFm.displayJavadocAndSource;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.displayCore.api.AbstractDisplayerWithLabel;
import org.softwareFm.displayCore.api.BindingContext;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.displayCore.api.IRegisteredItems;

public class SourceDisplayer extends AbstractDisplayerWithLabel<SourcePanel> {

	@Override
	public SourcePanel createLargeControl(DisplayerContext context, IRegisteredItems registeredItems, Composite parent, DisplayerDetails displayerDetails) {
		return new SourcePanel(parent, SWT.NULL, context, displayerDetails, DisplaySourceConstants.repositoryKey);
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, SourcePanel largeControl, Object value) {
		largeControl.setValue(bindingContext);
	}
}