package org.softwarefm.core;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.softwarefm.core.actions.SfmActionState;
import org.softwarefm.core.cache.IArtifactDataCache;
import org.softwarefm.core.constants.ImageConstants;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.selection.ISelectedBindingManager;
import org.softwarefm.core.templates.ITemplateStore;
import org.softwarefm.core.url.IUrlStrategy;
import org.softwarefm.utilities.callbacks.ICallback2;
import org.softwarefm.utilities.constants.CommonConstants;
import org.softwarefm.utilities.maps.IHasCache;
import org.softwarefm.utilities.resources.IResourceGetter;

public class SoftwareFmContainer<S> implements IHasCache {

	public static <S> SoftwareFmContainer<S> make(IUrlStrategy urlStrategy, ISelectedBindingManager<S> manager, ICallback2<String, Integer> importPom, ICallback2<ArtifactData, Integer> importManually, ITemplateStore templateStore, IArtifactDataCache artifactDataCache, SfmActionState state, ImageRegistry imageRegistry) {
		return new SoftwareFmContainer<S>(IResourceGetter.Utils.resourceGetter(Marker.class, "text"), manager, importPom, importManually, urlStrategy, templateStore, artifactDataCache, state, imageRegistry);
	}

	public static <S> SoftwareFmContainer<S> makeForTests(Display display, IResourceGetter resourceGetter) {
		IUrlStrategy urlStrategy = IUrlStrategy.Utils.urlStrategy(CommonConstants.softwareFmHost);
		ImageRegistry imageRegistry = new ImageRegistry();
		ImageConstants.initializeImageRegistry(display, imageRegistry);
		return new SoftwareFmContainer<S>(resourceGetter, //
				ISelectedBindingManager.Utils.<S> noManager(), //
				ICallback2.Utils.<String, Integer> noCallback(), //
				ICallback2.Utils.<ArtifactData, Integer> noCallback(), //
				urlStrategy,//
				ITemplateStore.Utils.templateStore(urlStrategy),//
				IArtifactDataCache.Utils.artifactDataCache(), //
				new SfmActionState(), imageRegistry);
	}

	public static <S> SoftwareFmContainer<S> makeForTests(Display display) {
		return makeForTests(display, IResourceGetter.Utils.resourceGetter(Marker.class, "text"));
	}

	public final IResourceGetter resourceGetter;
	public final ISelectedBindingManager<S> selectedBindingManager;
	public final ICallback2<String, Integer> importPom;
	public final ICallback2<ArtifactData, Integer> importManually;
	public final IUrlStrategy urlStrategy;
	public final IArtifactDataCache artifactDataCache;
	public final SfmActionState state;
	public final ImageRegistry imageRegistry;

	public SoftwareFmContainer(IResourceGetter resourceGetter, ISelectedBindingManager<S> selectedBindingManager, ICallback2<String, Integer> importPom, ICallback2<ArtifactData, Integer> importManually, IUrlStrategy urlStrategy, ITemplateStore templateStore, IArtifactDataCache artifactDataCache, SfmActionState state, ImageRegistry imageRegistry) {
		this.resourceGetter = resourceGetter;
		this.selectedBindingManager = selectedBindingManager;
		this.importPom = importPom;
		this.importManually = importManually;
		this.urlStrategy = urlStrategy;
		this.artifactDataCache = artifactDataCache;
		this.state = state;
		this.imageRegistry = imageRegistry;
	}

	public void clearCaches() {
		artifactDataCache.clearCaches();
	}

}
