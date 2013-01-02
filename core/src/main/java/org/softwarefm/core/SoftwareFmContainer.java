package org.softwarefm.core;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.softwarefm.core.actions.SfmActionState;
import org.softwarefm.core.cache.IArtifactDataCache;
import org.softwarefm.core.constants.ImageConstants;
import org.softwarefm.core.friends.internal.SocialUsage;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.selection.ISelectedBindingManager;
import org.softwarefm.core.templates.ITemplateStore;
import org.softwarefm.core.url.IUrlStrategy;
import org.softwarefm.shared.social.ISocialManager;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.shared.usage.UsageFromServer;
import org.softwarefm.utilities.callbacks.ICallback2;
import org.softwarefm.utilities.constants.CommonConstants;
import org.softwarefm.utilities.events.IMultipleListenerList;
import org.softwarefm.utilities.maps.IHasCache;
import org.softwarefm.utilities.resources.IResourceGetter;

public class SoftwareFmContainer<S> implements IHasCache {

	public static <S> SoftwareFmContainer<S> make(IUrlStrategy urlStrategy, ISelectedBindingManager<S> manager, ISocialManager socialManager, ICallback2<String, Integer> importPom, ICallback2<ArtifactData, Integer> importManually, ITemplateStore templateStore, IArtifactDataCache artifactDataCache, SfmActionState state, ImageRegistry imageRegistry, UsageFromServer usageFromServer) {
		IMultipleListenerList list = IMultipleListenerList.Utils.defaultList();
		IUsagePersistance persistance = IUsagePersistance.Utils.persistance();
		return new SoftwareFmContainer<S>(IResourceGetter.Utils.resourceGetter(Marker.class, "text"), manager, importPom, importManually, urlStrategy, templateStore, artifactDataCache, state, imageRegistry, list, persistance, usageFromServer, socialManager);
	}

	public static <S> SoftwareFmContainer<S> makeForTests(Display display, IResourceGetter resourceGetter, ISocialManager socialManager) {
		IUrlStrategy urlStrategy = IUrlStrategy.Utils.urlStrategy(CommonConstants.softwareFmHost);
		ImageRegistry imageRegistry = new ImageRegistry();
		ImageConstants.initializeImageRegistry(display, imageRegistry);
		IMultipleListenerList list = IMultipleListenerList.Utils.defaultList();
		IUsagePersistance persistance = IUsagePersistance.Utils.persistance();
		UsageFromServer usageFromServer = null;
		return new SoftwareFmContainer<S>(resourceGetter, //
				ISelectedBindingManager.Utils.<S> noManager(), //
				ICallback2.Utils.<String, Integer> noCallback(), //
				ICallback2.Utils.<ArtifactData, Integer> noCallback(), //
				urlStrategy,//
				ITemplateStore.Utils.templateStore(urlStrategy),//
				IArtifactDataCache.Utils.artifactDataCache(), //
				new SfmActionState(), imageRegistry, list, persistance, usageFromServer, socialManager);
	}

	public static <S> SoftwareFmContainer<S> makeForTests(Display display) {
		return makeForTests(display, IResourceGetter.Utils.resourceGetter(Marker.class, "text"), ISocialManager.Utils.socialManager(IMultipleListenerList.Utils.defaultList(), IUsagePersistance.Utils.persistance()));
	}

	public final IResourceGetter resourceGetter;
	public final ISelectedBindingManager<S> selectedBindingManager;
	public final ICallback2<String, Integer> importPom;
	public final ICallback2<ArtifactData, Integer> importManually;
	public final IUrlStrategy urlStrategy;
	public final IArtifactDataCache artifactDataCache;
	public final SfmActionState state;
	public final ImageRegistry imageRegistry;
	public final IMultipleListenerList listenerList;
	public final ISocialManager socialManager;
	public final IUsagePersistance persistance;
	public final UsageFromServer usageFromServer;
	public final SocialUsage socialUsage;

	public SoftwareFmContainer(IResourceGetter resourceGetter, ISelectedBindingManager<S> selectedBindingManager, ICallback2<String, Integer> importPom, ICallback2<ArtifactData, Integer> importManually, IUrlStrategy urlStrategy, ITemplateStore templateStore, IArtifactDataCache artifactDataCache, SfmActionState state, ImageRegistry imageRegistry, IMultipleListenerList listenerList, IUsagePersistance persistance, UsageFromServer usageFromServer, ISocialManager socialManager) {
		this.resourceGetter = resourceGetter;
		this.selectedBindingManager = selectedBindingManager;
		this.importPom = importPom;
		this.importManually = importManually;
		this.urlStrategy = urlStrategy;
		this.artifactDataCache = artifactDataCache;
		this.state = state;
		this.imageRegistry = imageRegistry;
		this.listenerList = listenerList;
		this.socialManager = socialManager;
		this.persistance = persistance;
		this.usageFromServer = usageFromServer;
		this.socialUsage = new SocialUsage(listenerList, urlStrategy, selectedBindingManager, socialManager);
	}

	public void clearCaches() {
		artifactDataCache.clearCaches();
	}

}
