package org.arc4eclipse.displayCore.api;

import org.arc4eclipse.displayCore.api.impl.ITopButtonState;
import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public abstract class AbstractDisplayerWithLabel<L extends Control> implements IDisplayer<L, Label> {

	@Override
	public Label createSmallControl(DisplayerContext displayerContext, final ITopButtonState topButtonState, Composite parent, String entity, final NameSpaceAndName nameSpaceAndName, String title) {
		final Label label = new Label(parent, SWT.BORDER);
		boolean intialState = topButtonState.state(nameSpaceAndName);
		label.setText(nameSpaceAndName.name);
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				boolean state = topButtonState.toogleState(nameSpaceAndName);
				updateLabel(label, state);
			}
		});
		updateLabel(label, intialState);
		return label;
	}

	private void updateLabel(Label label, boolean intialState) {
		Color color = intialState ? null : label.getDisplay().getSystemColor(SWT.COLOR_GRAY);
		label.setForeground(color);
	}

	@Override
	public void populateSmallControl(BindingContext bindingContext, Label smallControl, Object value) {
	}

	protected BindingRipperResult getBindingRipperResult(BindingContext bindingContext) {
		return (BindingRipperResult) bindingContext.context.get(DisplayCoreConstants.ripperResult);
	}

}