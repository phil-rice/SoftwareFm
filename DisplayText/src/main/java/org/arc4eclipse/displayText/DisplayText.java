package org.arc4eclipse.displayText;

import java.util.Collections;
import java.util.Map;

import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.IDisplayer;
import org.arc4eclipse.displayCore.api.NameSpaceNameAndValue;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class DisplayText implements IDisplayer {

	@Override
	public String getNameSpace() {
		return "text";
	}

	@Override
	public Control makeCompositeAsChildOf(Composite parent, final BindingContext bindingContext, final String url, Map<String, Object> data, final NameSpaceNameAndValue nameSpaceNameAndValue) {
		String title = bindingContext.titleLookup.getTitle(nameSpaceNameAndValue.nameSpace, nameSpaceNameAndValue.name);
		final TitleAndTextField textField = new TitleAndTextField(parent, SWT.NULL, title);
		textField.setText(nameSpaceNameAndValue.value.toString());
		textField.addCrListener(new Listener() {
			@Override
			public void handleEvent(Event e) {
				bindingContext.repository.modifyData(url, nameSpaceNameAndValue.key, textField.getText(), Collections.<String, Object> emptyMap());
			}
		});
		return textField;
	}

}