package org.arc4eclipse.displayJavadoc;

import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.api.AbstractDisplayerWithLabel;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.DisplayerDetails;
import org.arc4eclipse.displayCore.api.IRegisteredItems;
import org.arc4eclipse.utilities.strings.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

public class DisplayJavadoc extends AbstractDisplayerWithLabel<JavadocPanel> {

	@Override
	public String getNameSpace() {
		return RepositoryConstants.javadocKey;
	}

	@Override
	public JavadocPanel createLargeControl(DisplayerContext context, IRegisteredItems registeredItems, Composite parent, DisplayerDetails displayerDetails) {
		return new JavadocPanel(parent, SWT.BORDER, context, displayerDetails);
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, JavadocPanel largeControl, Object value) {
		largeControl.setValue(bindingContext.url, getBindingRipperResult(bindingContext), Strings.nullSafeToString(value));

	}

	@Override
	protected Image createMainImage(Device device) {
		return makeImage(device, "javadoc.png");
	}

	@Override
	protected Image createDepressedImage(Device device) {
		return makeImage(device, "javadoc depress.png");
	}
}