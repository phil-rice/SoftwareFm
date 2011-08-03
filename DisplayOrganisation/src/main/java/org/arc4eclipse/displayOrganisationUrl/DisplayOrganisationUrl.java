package org.arc4eclipse.displayOrganisationUrl;

import org.arc4eclipse.displayCore.api.AbstractDisplayerWithLabel;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.NameSpaceAndName;
import org.arc4eclipse.utilities.strings.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class DisplayOrganisationUrl extends AbstractDisplayerWithLabel<OrganisationPanel> {

	@Override
	public String getNameSpace() {
		return "organisationUrl";
	}

	@Override
	public OrganisationPanel createLargeControl(DisplayerContext context, Composite parent, NameSpaceAndName nameSpaceAndName, String title) {
		return new OrganisationPanel(parent, SWT.BORDER, context, nameSpaceAndName, title);
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, OrganisationPanel largeControl, Object value) {
		largeControl.setValue(bindingContext.url, Strings.nullSafeToString(value));
	}
}