package org.arc4eclipse.displayText;

import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.BoundTitleAndTextField;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.IDisplayer;
import org.arc4eclipse.displayCore.api.NameSpaceAndName;
import org.arc4eclipse.utilities.strings.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class DisplayText implements IDisplayer<BoundTitleAndTextField, Label> {

	@Override
	public String getNameSpace() {
		return "text";
	}

	@Override
	public Label createSmallControl(DisplayerContext displayerContext, Composite parent, NameSpaceAndName nameSpaceAndName, String title) {
		Label label = new Label(parent, SWT.BORDER);
		label.setText(nameSpaceAndName.name);
		return label;
	}

	@Override
	public BoundTitleAndTextField createLargeControl(DisplayerContext displayerContext, Composite parent, NameSpaceAndName nameSpaceAndName, String title) {
		return new BoundTitleAndTextField(parent, displayerContext, nameSpaceAndName, title);
	}

	@Override
	public void populateSmallControl(BindingContext bindingContext, Label smallControl, Object value) {
	}

	@Override
	public void populateLargeControl(BindingContext bindingContext, BoundTitleAndTextField largeControl, Object value) {
		largeControl.setUrl(bindingContext.url);
		largeControl.setText(Strings.nullSafeToString(value));
	}

}