package org.softwareFm.displayCore.api;

import java.util.Map;

import org.softwareFm.arc4eclipseRepository.api.RepositoryDataItemStatus;

public class BindingContext {

	public final String url;
	public final Map<String, Object> data;
	public final Map<String, Object> context;
	public final RepositoryDataItemStatus status;

	public BindingContext(RepositoryDataItemStatus status, String url, Map<String, Object> data, Map<String, Object> context) {
		this.status = status;
		this.url = url;
		this.data = data;
		this.context = context;
	}

}
