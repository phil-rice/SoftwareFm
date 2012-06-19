package org.softwarefm.eclipse;

import org.softwarefm.eclipse.actions.SfmActionState;
import org.softwarefm.eclipse.cache.IArtifactDataCache;
import org.softwarefm.eclipse.jdtBinding.ArtifactData;
import org.softwarefm.eclipse.selection.ISelectedBindingManager;
import org.softwarefm.eclipse.templates.ITemplateStore;
import org.softwarefm.eclipse.url.IUrlStrategy;
import org.softwarefm.utilities.callbacks.ICallback2;
import org.softwarefm.utilities.constants.CommonConstants;
import org.softwarefm.utilities.maps.IHasCache;
import org.softwarefm.utilities.resources.IResourceGetter;

public class SoftwareFmContainer<S> implements IHasCache {

	public static <S> SoftwareFmContainer<S> make(IUrlStrategy urlStrategy, ISelectedBindingManager<S> manager, ICallback2<String, Integer> importPom, ICallback2<ArtifactData, Integer> importManually, ITemplateStore templateStore, IArtifactDataCache artifactDataCache, SfmActionState state) {
		return new SoftwareFmContainer<S>(IResourceGetter.Utils.resourceGetter(Marker.class, "text"), manager, importPom, importManually, urlStrategy, templateStore, artifactDataCache, state);
	}

	public static <S> SoftwareFmContainer<S> makeForTests(IResourceGetter resourceGetter) {
		IUrlStrategy urlStrategy = IUrlStrategy.Utils.urlStrategy(CommonConstants.softwareFmHost);
		return new SoftwareFmContainer<S>(resourceGetter, //
				ISelectedBindingManager.Utils.<S> noManager(), //
				ICallback2.Utils.<String, Integer> noCallback(), //
				ICallback2.Utils.<ArtifactData, Integer> noCallback(), //
				urlStrategy,//
				ITemplateStore.Utils.templateStore(urlStrategy),//
				IArtifactDataCache.Utils.artifactDataCache(), //
				new SfmActionState());
	}

	public static <S> SoftwareFmContainer<S> makeForTests() {
		return makeForTests(IResourceGetter.Utils.resourceGetter(Marker.class, "text"));
	}

	public final IResourceGetter resourceGetter;
	public final ISelectedBindingManager<S> selectedBindingManager;
	public final ICallback2<String, Integer> importPom;
	public final ICallback2<ArtifactData, Integer> importManually;
	public final IUrlStrategy urlStrategy;
	public final IArtifactDataCache artifactDataCache;
	public final SfmActionState state;

	public SoftwareFmContainer(IResourceGetter resourceGetter, ISelectedBindingManager<S> selectedBindingManager, ICallback2<String, Integer> importPom, ICallback2<ArtifactData, Integer> importManually, IUrlStrategy urlStrategy, ITemplateStore templateStore, IArtifactDataCache artifactDataCache, SfmActionState state) {
		this.resourceGetter = resourceGetter;
		this.selectedBindingManager = selectedBindingManager;
		this.importPom = importPom;
		this.importManually = importManually;
		this.urlStrategy = urlStrategy;
		this.artifactDataCache = artifactDataCache;
		this.state = state;
	}

	public void clearCaches() {
		artifactDataCache.clearCaches();
	}

}
