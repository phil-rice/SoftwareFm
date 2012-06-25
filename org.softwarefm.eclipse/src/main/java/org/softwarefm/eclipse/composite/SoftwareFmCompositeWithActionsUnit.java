package org.softwarefm.eclipse.composite;

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
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.actions.SfmActionState;
import org.softwarefm.eclipse.actions.internal.ActionBarComposite;
import org.softwarefm.eclipse.actions.internal.SfmActionBarConfigurator;
import org.softwarefm.eclipse.cache.IArtifactDataCache;
import org.softwarefm.eclipse.link.IMakeLink;
import org.softwarefm.eclipse.maven.IMaven;
import org.softwarefm.eclipse.selection.ISelectedBindingManager;
import org.softwarefm.eclipse.selection.ISelectedBindingStrategy;
import org.softwarefm.eclipse.selection.internal.SelectedArtifactSelectionManager;
import org.softwarefm.eclipse.selection.internal.SwtThreadSelectedBindingAggregator;
import org.softwarefm.eclipse.swt.HasComposite;
import org.softwarefm.eclipse.swt.ISituationListAndBuilder;
import org.softwarefm.eclipse.swt.Swts;
import org.softwarefm.eclipse.templates.ITemplateStore;
import org.softwarefm.eclipse.url.IUrlStrategy;
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
