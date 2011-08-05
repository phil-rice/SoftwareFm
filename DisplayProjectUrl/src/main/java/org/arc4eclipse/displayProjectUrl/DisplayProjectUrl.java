package org.arc4eclipse.displayProjectUrl;

import org.arc4eclipse.displayCore.api.AbstractDisplayerWithLabel;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.NameSpaceAndName;
import org.arc4eclipse.utilities.strings.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

public class DisplayProjectUrl extends AbstractDisplayerWithLabel<ProjectUrlPanel> {

	@Override
	public String getNameSpace() {
		return "projectUrl";
	}

	@Override
	public ProjectUrlPanel createLargeControl(DisplayerContext context, Composite parent, String entity, NameSpaceAndName nameSpaceAndName, String title) {
		return new ProjectUrlPanel(parent, SWT.BORDER, context, entity, nameSpaceAndName, title);
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, ProjectUrlPanel largeControl, Object value) {
		largeControl.setValue(bindingContext.url, Strings.nullSafeToString(value));
	}

	@Override
	protected Image createMainImage(Device device) {
		return makeImage(device, "red cross.png");
	}

	@Override
	protected Image createDepressedImage(Device device) {
		return makeImage(device, "red cross.png");
	}
}