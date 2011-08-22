package org.arc4eclipse.displayMerchandising;

import org.arc4eclipse.displayCore.api.AbstractDisplayerWithLabel;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.DisplayerDetails;
import org.arc4eclipse.displayCore.api.IRegisteredItems;
import org.arc4eclipse.displayLists.ListPanel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

public class DisplayMerchandising extends AbstractDisplayerWithLabel<ListPanel> {

	@Override
	public String getNameSpace() {
		return "merchandising";
	}

	@Override
	public ListPanel createLargeControl(DisplayerContext context, IRegisteredItems registeredItems, Composite parent, DisplayerDetails displayerDetails) {
		return new ListPanel(parent, SWT.BORDER, context, displayerDetails);
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, ListPanel largeControl, Object value) {
		largeControl.setValue(bindingContext.url, value);

	}

	@Override
	protected Image createMainImage(Device device) {
		return makeImage(device, "merchandise.png");
	}

	@Override
	protected Image createDepressedImage(Device device) {
		return makeImage(device, "mercahndise depess.png");
	}
}