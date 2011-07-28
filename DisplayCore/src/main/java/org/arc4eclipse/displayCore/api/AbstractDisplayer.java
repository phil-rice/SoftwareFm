package org.arc4eclipse.displayCore.api;

import java.util.Collections;

public abstract class AbstractDisplayer implements IDisplayer {
	private final BindingContext context;
	private final String url;
	private final NameSpaceNameAndValue nameSpaceNameAndValue;

	public AbstractDisplayer(int arg1, String title, final BindingContext context, String url, final NameSpaceNameAndValue nameSpaceNameAndValue) {
		this.context = context;
		this.url = url;
		this.nameSpaceNameAndValue = nameSpaceNameAndValue;
	}

	protected void update(Object value) {
		context.repository.modifyData(url, nameSpaceNameAndValue.key, value, Collections.<String, Object> emptyMap());
	}
}
