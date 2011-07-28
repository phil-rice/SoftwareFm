package org.arc4eclipse.displayCore.api;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;

public class BindingContext {

	public final IArc4EclipseRepository repository;
	public ITitleLookup titleLookup;

	public BindingContext(IArc4EclipseRepository repository, ITitleLookup titleLookup) {
		this.repository = repository;
		this.titleLookup = titleLookup;
	}

}
