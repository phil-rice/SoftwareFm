package org.arc4eclipse.displayForums;

import org.arc4eclipse.displayCore.api.AbstractDisplayerWithLabel;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.DisplayerDetails;
import org.arc4eclipse.displayLists.ListPanel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

public class DisplayTutorials extends AbstractDisplayerWithLabel<ListPanel> {

	@Override
	public String getNameSpace() {
		return "tutorials";
	}

	@Override
	public ListPanel createLargeControl(DisplayerContext context, Composite parent, DisplayerDetails displayerDetails) {
		return new ListPanel(parent, SWT.BORDER, context, displayerDetails);
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, ListPanel largeControl, Object value) {
		largeControl.setValue(bindingContext.url, value);

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