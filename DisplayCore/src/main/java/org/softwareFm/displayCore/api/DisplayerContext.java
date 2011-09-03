package org.softwareFm.displayCore.api;

import org.eclipse.jface.resource.ImageRegistry;
import org.softwareFm.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.softwareFm.arc4eclipseRepository.api.IUrlGeneratorMap;
import org.softwareFm.panel.ISelectedBindingManager;
import org.softwareFm.swtBasics.text.ConfigForTitleAnd;
import org.softwareFm.utilities.resources.IResourceGetter;

public class DisplayerContext {
	public final ImageRegistry imageRegistry;
	public final ISelectedBindingManager selectedBindingManager;
	public final IArc4EclipseRepository repository;
	public final ConfigForTitleAnd configForTitleAnd;
	public final IResourceGetter resourceGetter;
	public final IUrlGeneratorMap urlGeneratorMap;

	public DisplayerContext(ISelectedBindingManager selectedBindingManager, IArc4EclipseRepository repository, IUrlGeneratorMap urlGeneratorMap, ConfigForTitleAnd configForTitleAnd) {
		this.urlGeneratorMap = urlGeneratorMap;
		this.imageRegistry = configForTitleAnd.imageRegistry;
		this.selectedBindingManager = selectedBindingManager;
		this.repository = repository;
		this.configForTitleAnd = configForTitleAnd;
		this.resourceGetter = configForTitleAnd.resourceGetter;
	}

}
