package org.arc4eclipse.displayText;

import org.arc4eclipse.displayCore.api.AbstractDisplayerWithLabel;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.BoundTitleAndTextField;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.NameSpaceAndName;
import org.arc4eclipse.utilities.strings.Strings;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

public class DisplayText extends AbstractDisplayerWithLabel<BoundTitleAndTextField> {

	@Override
	public String getNameSpace() {
		return "text";
	}

	@Override
	public BoundTitleAndTextField createLargeControl(DisplayerContext context, Composite parent, String entity, NameSpaceAndName nameSpaceAndName, String title) {
		return new BoundTitleAndTextField(parent, context, entity, nameSpaceAndName, title);
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, BoundTitleAndTextField largeControl, Object value) {
		largeControl.setUrl(bindingContext.url);
		largeControl.setText(Strings.nullSafeToString(value));
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