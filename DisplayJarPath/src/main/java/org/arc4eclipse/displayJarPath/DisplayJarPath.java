package org.arc4eclipse.displayJarPath;

import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.api.AbstractDisplayerWithLabel;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.DisplayerDetails;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

public class DisplayJarPath extends AbstractDisplayerWithLabel<DisplayJarPanel> {

	@Override
	public String getNameSpace() {
		return RepositoryConstants.jarDetailsKey;
	}

	@Override
	public DisplayJarPanel createLargeControl(DisplayerContext context, Composite parent, DisplayerDetails displayerDetails) {
		return new DisplayJarPanel(parent, SWT.BORDER, context);
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, DisplayJarPanel largeControl, Object value) {
		BindingRipperResult ripped = getBindingRipperResult(bindingContext);
		largeControl.setValue(bindingContext.url, ripped);
	}

	@Override
	protected Image createMainImage(Device device) {
		return makeImage(device, "Jar.png");
	}

	@Override
	protected Image createDepressedImage(Device device) {
		return makeImage(device, "Jar depress.png");
	}
}