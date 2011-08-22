package org.arc4eclipse.displayCore.api;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.panel.ISelectedBindingManager;
import org.arc4eclipse.swtBasics.text.ConfigForTitleAnd;
import org.arc4eclipse.utilities.resources.IResourceGetter;
import org.eclipse.jface.resource.ImageRegistry;

public class DisplayerContext {
	public final ImageRegistry imageRegistry;
	public final ISelectedBindingManager selectedBindingManager;
	public final IArc4EclipseRepository repository;
	public final ConfigForTitleAnd configForTitleAnd;
	public final IResourceGetter resourceGetter;

	public DisplayerContext(ISelectedBindingManager selectedBindingManager, IArc4EclipseRepository repository, ConfigForTitleAnd configForTitleAnd) {
		this.imageRegistry = configForTitleAnd.imageRegistry;
		this.selectedBindingManager = selectedBindingManager;
		this.repository = repository;
		this.configForTitleAnd = configForTitleAnd;
		this.resourceGetter = configForTitleAnd.resourceGetter;
	}

}
