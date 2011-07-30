package org.arc4eclipse.displayText;

import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.BoundTitleAndTextField;
import org.arc4eclipse.displayCore.api.IDisplayer;
import org.arc4eclipse.displayCore.api.NameSpaceNameAndValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class DisplayText implements IDisplayer {

	@Override
	public String getNameSpace() {
		return "text";
	}

	@Override
	public Control makeCompositeAsChildOf(Composite parent, final BindingContext bindingContext, final NameSpaceNameAndValue nameSpaceNameAndValue) {
		return new BoundTitleAndTextField(parent, SWT.NULL, bindingContext, nameSpaceNameAndValue);
	}

}