package org.softwareFm.displayJavadocAndSource;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.displayCore.api.AbstractDisplayerWithSummaryIcon;
import org.softwareFm.displayCore.api.BindingContext;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.displayCore.api.Displayers;
import org.softwareFm.displayCore.api.IDisplayContainerFactoryGetter;
import org.softwareFm.displayCore.api.IRegisteredItems;
import org.softwareFm.displayCore.api.SummaryIcon;
import org.softwareFm.swtBasics.images.Resources;

public class SourceDisplayer extends AbstractDisplayerWithSummaryIcon<SourcePanel, SummaryIcon> {

	@Override
	public SourcePanel createLargeControl(Composite parent, DisplayerContext context, IRegisteredItems registeredItems, IDisplayContainerFactoryGetter displayContainerFactoryGetter, DisplayerDetails displayerDetails) {
		return new SourcePanel(parent, SWT.NULL, context, displayerDetails);
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, SourcePanel largeControl, Object value) {
		largeControl.setValue(bindingContext);
	}

	public static void main(String[] args) {
		Displayers.displayWithKey1(new SourceDisplayer(), Resources.resourceGetterWithBasics("org.softwareFm.displayCore.api.DisplayForTest", "org.softwareFm.displayJavadocAndSource.JavadocAndSource"), "text");
	}
}