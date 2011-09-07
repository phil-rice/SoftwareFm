package org.softwareFm.displayJavadocAndSource;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.displayCore.api.AbstractDisplayerWithLabel;
import org.softwareFm.displayCore.api.BindingContext;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.displayCore.api.Displayers;
import org.softwareFm.displayCore.api.IRegisteredItems;
import org.softwareFm.swtBasics.images.Resources;

public class JavadocDisplayer extends AbstractDisplayerWithLabel<JavadocPanel> {

	@Override
	public JavadocPanel createLargeControl(DisplayerContext context, IRegisteredItems registeredItems, Composite parent, DisplayerDetails displayerDetails) {
		return new JavadocPanel(parent, SWT.NULL, context, displayerDetails);
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, JavadocPanel largeControl, Object value) {
		largeControl.setValue(bindingContext);
	}

	public static void main(String[] args) {
		Displayers.displayWithKey1(new JavadocDisplayer(), Resources.resourceGetterWithBasics("org.softwareFm.displayCore.api.DisplayForTest", "org.softwareFm.displayJavadocAndSource.JavadocAndSource"), "text");
	}
}