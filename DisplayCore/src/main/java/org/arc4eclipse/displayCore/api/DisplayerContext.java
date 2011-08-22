package org.arc4eclipse.displayCore.api;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.panel.ISelectedBindingManager;

public class DisplayerContext {
	public final ISelectedBindingManager selectedBindingManager;
	public final IArc4EclipseRepository repository;

	public DisplayerContext(ISelectedBindingManager selectedBindingManager, IArc4EclipseRepository repository) {
		this.selectedBindingManager = selectedBindingManager;
		this.repository = repository;
	}

}
