package org.arc4eclipse.displayText;

import org.arc4eclipse.displayCore.api.IDisplayer;
import org.arc4eclipse.displayCore.api.ITitleLookup;
import org.arc4eclipse.displayCore.api.NameSpaceNameAndValue;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class DisplayText implements IDisplayer {

	@Override
	public String getNameSpace() {
		return "text";
	}

	@Override
	public Control makeCompositeAsChildOf(ITitleLookup titleLookup, Composite parent, NameSpaceNameAndValue nameSpaceNameAndValue) {
		String title = titleLookup.getTitle(nameSpaceNameAndValue.nameSpace, nameSpaceNameAndValue.name);
		TitleAndTextField textField = new TitleAndTextField(parent, SWT.NULL, title);
		textField.setText(nameSpaceNameAndValue.value.toString());
		return textField;
	}

}