package org.softwarefm.eclipse;

import org.softwarefm.eclipse.selection.ISelectedBindingManager;
import org.softwarefm.utilities.resources.IResourceGetter;

public class SoftwareFmContainer<S> {
	public final IResourceGetter resourceGetter;
	public final ISelectedBindingManager<S> selectedBindingManager;

	public SoftwareFmContainer(IResourceGetter resourceGetter, ISelectedBindingManager<S> selectedBindingManager) {
		super();
		this.resourceGetter = resourceGetter;
		this.selectedBindingManager = selectedBindingManager;
	}

}
