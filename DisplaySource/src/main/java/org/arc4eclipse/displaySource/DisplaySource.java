package org.arc4eclipse.displaySource;

import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.api.AbstractDisplayerWithLabel;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.NameSpaceAndName;
import org.arc4eclipse.utilities.strings.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class DisplaySource extends AbstractDisplayerWithLabel<SourcePanel> {

	@Override
	public String getNameSpace() {
		return RepositoryConstants.sourceKey;
	}

	@Override
	public SourcePanel createLargeControl(DisplayerContext context, Composite parent, NameSpaceAndName nameSpaceAndName, String title) {
		return new SourcePanel(parent, SWT.BORDER, context, nameSpaceAndName, title);
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, SourcePanel largeControl, Object value) {
		largeControl.setValue(bindingContext.url, getBindingRipperResult(bindingContext), Strings.nullSafeToString(value));

	}
}