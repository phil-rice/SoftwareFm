package org.softwarefm.eclipse;

import org.softwarefm.eclipse.selection.ISelectedBindingManager;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.resources.IResourceGetter;

public class SoftwareFmContainer<S> {

	public static <S> SoftwareFmContainer<S> make(ISelectedBindingManager<S> manager, ICallback<String> importPom) {
		return new SoftwareFmContainer<S>(IResourceGetter.Utils.resourceGetter(BundleMarker.class, "text"), manager, importPom);
	}

	public static <S> SoftwareFmContainer<S> makeForTests() {
		return new SoftwareFmContainer<S>(IResourceGetter.Utils.resourceGetter(BundleMarker.class, "text"), ISelectedBindingManager.Utils.<S> noManager(), ICallback.Utils.<String> memory());
	}

	public final IResourceGetter resourceGetter;
	public final ISelectedBindingManager<S> selectedBindingManager;
	public final ICallback<String> importPom;

	public SoftwareFmContainer(IResourceGetter resourceGetter, ISelectedBindingManager<S> selectedBindingManager, ICallback<String> importPom) {
		super();
		this.resourceGetter = resourceGetter;
		this.selectedBindingManager = selectedBindingManager;
		this.importPom = importPom;
	}

}
