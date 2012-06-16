package org.softwarefm.eclipse;

import org.softwarefm.eclipse.cache.IProjectDataCache;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.ISelectedBindingManager;
import org.softwarefm.eclipse.templates.ITemplateStore;
import org.softwarefm.eclipse.url.IUrlStrategy;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.constants.CommonConstants;
import org.softwarefm.utilities.maps.IHasCache;
import org.softwarefm.utilities.resources.IResourceGetter;

public class SoftwareFmContainer<S> implements IHasCache {

	public static <S> SoftwareFmContainer<S> make(IUrlStrategy urlStrategy, ISelectedBindingManager<S> manager, ICallback<String> importPom, ICallback<ProjectData> importManually, ITemplateStore templateStore,IProjectDataCache projectDataCache) {
		return new SoftwareFmContainer<S>(IResourceGetter.Utils.resourceGetter(Marker.class, "text"), manager, importPom, importManually, urlStrategy, templateStore, projectDataCache);
	}

	public static <S> SoftwareFmContainer<S> makeForTests(IResourceGetter resourceGetter) {
		IUrlStrategy urlStrategy = IUrlStrategy.Utils.urlStrategy(CommonConstants.softwareFmHost);
		return new SoftwareFmContainer<S>(resourceGetter, //
				ISelectedBindingManager.Utils.<S> noManager(), //
				ICallback.Utils.<String> memory(), //
				ICallback.Utils.<ProjectData> memory(), //
				urlStrategy,//
				ITemplateStore.Utils.templateStore(urlStrategy),//
				IProjectDataCache.Utils.projectDataCache());
	}

	public static <S> SoftwareFmContainer<S> makeForTests() {
		return makeForTests(IResourceGetter.Utils.resourceGetter(Marker.class, "text"));
	}

	public final IResourceGetter resourceGetter;
	public final ISelectedBindingManager<S> selectedBindingManager;
	public final ICallback<String> importPom;
	public final ICallback<ProjectData> importManually;
	public final IUrlStrategy urlStrategy;
	public final IProjectDataCache projectDataCache;

	public SoftwareFmContainer(IResourceGetter resourceGetter, ISelectedBindingManager<S> selectedBindingManager, ICallback<String> importPom, ICallback<ProjectData> importManually, IUrlStrategy urlStrategy, ITemplateStore templateStore, IProjectDataCache projectDataCache) {
		this.resourceGetter = resourceGetter;
		this.selectedBindingManager = selectedBindingManager;
		this.importPom = importPom;
		this.importManually = importManually;
		this.urlStrategy = urlStrategy;
		this.projectDataCache = projectDataCache;
	}

	public void clearCaches() {
		projectDataCache.clearCaches();

	}


}
