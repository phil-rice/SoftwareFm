package org.softwareFm.displayCore.api;

import java.util.Collections;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.softwareFm.swtBasics.text.TitleAndTextField;

public class BoundTitleAndTextField extends TitleAndTextField {

	private String url;

	public BoundTitleAndTextField(Composite parent, final DisplayerContext displayerContext, final DisplayerDetails displayerDetails) {
		super(displayerContext.configForTitleAnd, parent, displayerDetails.key);
		addCrListener(new Listener() {
			@Override
			public void handleEvent(Event e) {
				displayerContext.repository.modifyData(displayerDetails.entity, url, displayerDetails.key, getText(), Collections.<String, Object> emptyMap());
			}
		});
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
