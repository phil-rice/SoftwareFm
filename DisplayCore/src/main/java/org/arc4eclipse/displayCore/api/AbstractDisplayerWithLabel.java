package org.arc4eclipse.displayCore.api;

import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public abstract class AbstractDisplayerWithLabel<L extends Control> implements IDisplayer<L, Label> {

	@Override
	public Label createSmallControl(DisplayerContext displayerContext, Composite parent, String entity, NameSpaceAndName nameSpaceAndName, String title) {
		Label label = new Label(parent, SWT.BORDER);
		label.setText(nameSpaceAndName.name);
		return label;
	}

	@Override
	public void populateSmallControl(BindingContext bindingContext, Label smallControl, Object value) {
	}

	protected BindingRipperResult getBindingRipperResult(BindingContext bindingContext) {
		return (BindingRipperResult) bindingContext.context.get(DisplayCoreConstants.ripperResult);
	}

}