package org.softwarefm.eclipse;

import org.softwarefm.eclipse.selection.ISelectedBindingManager;
import org.softwarefm.utilities.resources.IResourceGetter;

public class SoftwareFmContainer<S> {

	public static <S> SoftwareFmContainer<S> make(ISelectedBindingManager<S> manager) {
		return new SoftwareFmContainer<S>(IResourceGetter.Utils.resourceGetter(BundleMarker.class, "text"), manager);
	}

	public static <S> SoftwareFmContainer<S> makeForTests() {
		return new SoftwareFmContainer<S>(IResourceGetter.Utils.resourceGetter(BundleMarker.class, "text"), ISelectedBindingManager.Utils.<S> noManager());
	}

	public final IResourceGetter resourceGetter;
	public final ISelectedBindingManager<S> selectedBindingManager;

	public SoftwareFmContainer(IResourceGetter resourceGetter, ISelectedBindingManager<S> selectedBindingManager) {
		super();
		this.resourceGetter = resourceGetter;
		this.selectedBindingManager = selectedBindingManager;
	}

}
