package org.arc4eclipse.displayCore.api;

import java.util.Map;

public class BindingContext {

	public final String url;
	public final Map<String, Object> data;
	public final Map<String, Object> context;

	public BindingContext(String url, Map<String, Object> data, Map<String, Object> context) {
		this.url = url;
		this.data = data;
		this.context = context;
	}

}
