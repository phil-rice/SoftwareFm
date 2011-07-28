package org.arc4eclipse.displayText;

import java.util.Map;

import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.IDisplayer;
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
	public Control makeCompositeAsChildOf(Composite parent, BindingContext bindingContext, Map<String, Object> data, NameSpaceNameAndValue nameSpaceNameAndValue) {
		String title = bindingContext.titleLookup.getTitle(nameSpaceNameAndValue.nameSpace, nameSpaceNameAndValue.name);
		TitleAndTextField textField = new TitleAndTextField(parent, SWT.NULL, title);
		textField.setText(nameSpaceNameAndValue.value.toString());
		return textField;
	}

}