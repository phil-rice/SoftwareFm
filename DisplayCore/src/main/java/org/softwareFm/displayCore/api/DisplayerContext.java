package org.softwareFm.displayCore.api;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.panel.ISelectedBindingManager;
import org.softwareFm.repository.api.ISoftwareFmRepository;
import org.softwareFm.repository.api.IUrlGeneratorMap;
import org.softwareFm.swtBasics.text.ConfigForTitleAnd;
import org.softwareFm.utilities.resources.IResourceGetter;

public class DisplayerContext {
	public final ImageRegistry imageRegistry;
	public final ISelectedBindingManager selectedBindingManager;
	public final ISoftwareFmRepository repository;
	public final ConfigForTitleAnd configForTitleAnd;
	public final IResourceGetter resourceGetter;
	public final IUrlGeneratorMap urlGeneratorMap;
	public final IDisplayContainerButtons displayContainerButtons;

	public DisplayerContext(ISelectedBindingManager selectedBindingManager, ISoftwareFmRepository repository, IUrlGeneratorMap urlGeneratorMap, ConfigForTitleAnd configForTitleAnd, IDisplayContainerButtons displayContainerButtons) {
		this.urlGeneratorMap = urlGeneratorMap;
		this.displayContainerButtons = displayContainerButtons;
		this.imageRegistry = configForTitleAnd.imageRegistry;
		this.selectedBindingManager = selectedBindingManager;
		this.repository = repository;
		this.configForTitleAnd = configForTitleAnd;
		this.resourceGetter = configForTitleAnd.resourceGetter;
	}

	public static class Utils {
		public static DisplayerContext forTest(Composite parent, IResourceGetter resourceGetter, ImageRegistry imageRegistry) {
			DisplayerContext displayerContext = new DisplayerContext(//
					ISelectedBindingManager.Utils.noSelectedBindingManager(), //
					ISoftwareFmRepository.Utils.repository(), //
					IUrlGeneratorMap.Utils.urlGeneratorMap(), //
					ConfigForTitleAnd.create(parent.getDisplay(), resourceGetter, imageRegistry),//
					IDisplayContainerButtons.Utils.makeButtons(parent, imageRegistry, resourceGetter));
			return displayerContext;

		}
	}

}
