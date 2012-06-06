package org.softwarefm.eclipse;

import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.ISelectedBindingManager;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.resources.IResourceGetter;

public class SoftwareFmContainer<S> {

	public static <S> SoftwareFmContainer<S> make(ISelectedBindingManager<S> manager, ICallback<String> importPom, ICallback<ProjectData> importManually) {
		return new SoftwareFmContainer<S>(IResourceGetter.Utils.resourceGetter(BundleMarker.class, "text"), manager, importPom, importManually);
	}

	public static <S>SoftwareFmContainer<S> makeForTests(IResourceGetter resourceGetter) {
		return new SoftwareFmContainer<S>(resourceGetter, ISelectedBindingManager.Utils.<S> noManager(), ICallback.Utils.<String> memory(), ICallback.Utils.<ProjectData> memory());
	}
	public static <S> SoftwareFmContainer<S> makeForTests() {
		return new SoftwareFmContainer<S>(IResourceGetter.Utils.resourceGetter(BundleMarker.class, "text"), ISelectedBindingManager.Utils.<S> noManager(), ICallback.Utils.<String> memory(), ICallback.Utils.<ProjectData> memory());
	}

	public final IResourceGetter resourceGetter;
	public final ISelectedBindingManager<S> selectedBindingManager;
	public final ICallback<String> importPom;
	public final ICallback<ProjectData> importManually;

	public SoftwareFmContainer(IResourceGetter resourceGetter, ISelectedBindingManager<S> selectedBindingManager, ICallback<String> importPom, ICallback<ProjectData> importManually) {
		this.resourceGetter = resourceGetter;
		this.selectedBindingManager = selectedBindingManager;
		this.importPom = importPom;
		this.importManually = importManually;
	}


}
