package org.arc4eclipse.displayCore.api;

import java.util.Collections;

import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class BoundTitleAndTextField extends TitleAndTextField {

	private String url;

	public BoundTitleAndTextField(Composite parent, DisplayerContext displayerContext, final NameSpaceAndName nameSpaceAndName, String title) {
		this(parent, SWT.BORDER, displayerContext, nameSpaceAndName, title);
	}

	public BoundTitleAndTextField(Composite parent, int style, final DisplayerContext displayerContext, final NameSpaceAndName nameSpaceAndName, String title) {
		super(parent, style, displayerContext.imageFactory, title, true);
		addCrListener(new Listener() {
			@Override
			public void handleEvent(Event e) {
				displayerContext.repository.modifyData(url, nameSpaceAndName.key, getText(), Collections.<String, Object> emptyMap());
			}
		});
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
