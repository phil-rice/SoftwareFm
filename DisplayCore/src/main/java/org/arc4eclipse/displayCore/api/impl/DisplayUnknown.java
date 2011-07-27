package org.arc4eclipse.displayCore.api.impl;

import org.arc4eclipse.displayCore.api.IDisplayer;
import org.arc4eclipse.displayCore.api.ITitleLookup;
import org.arc4eclipse.displayCore.api.NameSpaceNameAndValue;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class DisplayUnknown implements IDisplayer {

	@Override
	public String getNameSpace() {
		return "text";
	}

	@Override
	public Control makeCompositeAsChildOf(ITitleLookup titleLookup, Composite parent, NameSpaceNameAndValue nameSpaceNameAndValue) {
		TitleAndTextField textField = new TitleAndTextField(parent, SWT.NULL, "Don't know how to display " + nameSpaceNameAndValue.nameSpace + ":" + nameSpaceNameAndValue.name);
		textField.setText(nameSpaceNameAndValue.value.toString());
		textField.setEnabled(false);
		return textField;
	}

}