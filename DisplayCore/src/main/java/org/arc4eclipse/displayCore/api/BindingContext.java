package org.arc4eclipse.displayCore.api;

import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;

public class BindingContext {

	public final IArc4EclipseRepository repository;
	public ITitleLookup titleLookup;
	public final String url;
	public final Map<String, Object> data;
	public final Map<String, Object> context;

	public BindingContext(IArc4EclipseRepository repository, ITitleLookup titleLookup, String url, Map<String, Object> data, Map<String, Object> context) {
		this.repository = repository;
		this.titleLookup = titleLookup;
		this.url = url;
		this.data = data;
		this.context = context;
	}

}
