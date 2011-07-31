package org.arc4eclipse.displayCore.api;

import java.util.Collections;

import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class BoundTitleAndTextField extends TitleAndTextField {

	public BoundTitleAndTextField(Composite parent, int style, final BindingContext bindingContext, final NameSpaceNameAndValue nameSpaceNameAndValue) {
		super(parent, bindingContext.images, bindingContext.titleLookup.getTitle(nameSpaceNameAndValue.nameSpace, nameSpaceNameAndValue.name), true);
		setText(nameSpaceNameAndValue.value.toString());
		addCrListener(new Listener() {
			@Override
			public void handleEvent(Event e) {
				bindingContext.repository.modifyData(bindingContext.url, nameSpaceNameAndValue.key, getText(), Collections.<String, Object> emptyMap());
			}
		});
	}

}
