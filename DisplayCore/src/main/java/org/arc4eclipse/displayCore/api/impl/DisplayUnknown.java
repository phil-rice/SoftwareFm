package org.arc4eclipse.displayCore.api.impl;

import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.IDisplayer;
import org.arc4eclipse.displayCore.api.NameSpaceNameAndValue;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.arc4eclipse.utilities.strings.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class DisplayUnknown implements IDisplayer {

	@Override
	public String getNameSpace() {
		return "text";
	}

	@Override
	public Control makeCompositeAsChildOf(Composite parent, BindingContext bindingContext, NameSpaceNameAndValue nameSpaceNameAndValue) {
		TitleAndTextField textField = new TitleAndTextField(parent, SWT.NULL, bindingContext.images, "Don't know how to display " + nameSpaceNameAndValue.nameSpace + ":" + nameSpaceNameAndValue.name, false);
		textField.setText(Strings.nullSafeToString(nameSpaceNameAndValue.value));
		textField.setEnabled(false);
		return textField;
	}

}