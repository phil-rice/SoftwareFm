package org.softwarefm.core.composite;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.actions.SfmActionState;
import org.softwarefm.core.actions.internal.ActionBarComposite;
import org.softwarefm.core.actions.internal.SfmActionBarConfigurator;
import org.softwarefm.core.cache.IArtifactDataCache;
import org.softwarefm.core.link.IMakeLink;
import org.softwarefm.core.maven.IMaven;
import org.softwarefm.core.selection.ISelectedBindingManager;
import org.softwarefm.core.selection.ISelectedBindingStrategy;
import org.softwarefm.core.selection.internal.SelectedArtifactSelectionManager;
import org.softwarefm.core.selection.internal.SwtThreadSelectedBindingAggregator;
import org.softwarefm.core.swt.HasComposite;
import org.softwarefm.core.swt.ISituationListAndBuilder;
import org.softwarefm.core.swt.Swts;
import org.softwarefm.core.templates.ITemplateStore;
import org.softwarefm.core.url.IUrlStrategy;
import org.softwarefm.utilities.callbacks.ICallback;

public class SoftwareFmCompositeWithActionsUnit extends HasComposite {
	private final SoftwareFmContainer<?> container;

	@SuppressWarnings("unused")
	public SoftwareFmCompositeWithActionsUnit(Composite parent, SoftwareFmContainer<?> container, SfmActionState state) {
		super(parent);
		this.container = container;
		ActionBarComposite actionBar = new ActionBarComposite(getComposite());
		new SfmActionBarConfigurator(container.resourceGetter, state, container.selectedBindingManager).configure(actionBar);
		new AllComposite(getComposite(), container);
		Swts.Grid.addGrabHorizontalAndFillGridDataToAllChildrenWithLastGrabingVertical(getComposite());
	}

	public static void main(String[] args) {
		final ExecutorService threadingPool = Executors.newCachedThreadPool();
		try {
			Swts.Show.xUnit(SoftwareFmCompositeWithActionsUnit.class.getSimpleName(), new File("src/test/resources/org/softwarefm/eclipse/composite"), "dat", new ISituationListAndBuilder<SoftwareFmCompositeWithActionsUnit, String>() {
				public SoftwareFmCompositeWithActionsUnit makeChild(Composite parent) throws Exception {
					final SwtThreadSelectedBindingAggregator<Map<String, Object>> listenerManager = new SwtThreadSelectedBindingAggregator<Map<String, Object>>(new Shell().getDisplay());
					IArtifactDataCache artifactDataCache = IArtifactDataCache.Utils.artifactDataCache();
					SelectedArtifactSelectionManager<Map<String, Object>, Map<String, Object>> manager = new SelectedArtifactSelectionManager<Map<String, Object>, Map<String, Object>>(//
							listenerManager, //
							ISelectedBindingStrategy.Utils.fromMap(), //
							threadingPool, //
							artifactDataCache,//
							ICallback.Utils.rethrow());
					IUrlStrategy rawUrlStrategy = IUrlStrategy.Utils.urlStrategy();
					SfmActionState state = new SfmActionState();
					IUrlStrategy urlStrategy = IUrlStrategy.Utils.withActionBarState(rawUrlStrategy, state);
					ITemplateStore templateStore = ITemplateStore.Utils.templateStore(rawUrlStrategy);
					final IMakeLink makeLink = IMakeLink.Utils.makeLink(rawUrlStrategy, templateStore, artifactDataCache);
					ImageRegistry imageRegistry = new ImageRegistry(parent.getDisplay());
					SoftwareFmContainer<Map<String, Object>> container = SoftwareFmContainer.make(urlStrategy, //
							manager, //
							IMaven.Utils.importPomWithSysouts(makeLink, manager),//
							IMakeLink.Utils.manuallyImportWhenNotInEclipse(makeLink, manager),//
							templateStore, //
							artifactDataCache,//
							state,//
							imageRegistry);
					SoftwareFmCompositeWithActionsUnit softwareFmComposite = new SoftwareFmCompositeWithActionsUnit(parent, container, state);
					return softwareFmComposite;
				}

				@SuppressWarnings("unchecked")
				public void selected(SoftwareFmCompositeWithActionsUnit control, String context, String value) throws Exception {
					ISelectedBindingManager<Map<String, Object>> selectionBindingManager = (ISelectedBindingManager<Map<String, Object>>) control.container.selectedBindingManager;
					Map<String, Object> map = new HashMap<String, Object>();
					Properties properties = new Properties();
					properties.load(new ByteArrayInputStream(value.getBytes()));
					for (Enumeration<String> names = (Enumeration<String>) properties.propertyNames(); names.hasMoreElements();) {
						String name = names.nextElement();
						map.put(name, properties.getProperty(name));
					}
					selectionBindingManager.selectionOccured(map);
				}
			});
		} finally {
			threadingPool.shutdown();
		}
	}
}
