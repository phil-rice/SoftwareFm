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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.actions.SfmActionState;
import org.softwarefm.eclipse.cache.IArtifactDataCache;
import org.softwarefm.eclipse.constants.ImageConstants;
import org.softwarefm.eclipse.link.IMakeLink;
import org.softwarefm.eclipse.maven.IMaven;
import org.softwarefm.eclipse.selection.IHasSelectionBindingManager;
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
import org.softwarefm.utilities.functions.Functions;
import org.softwarefm.utilities.functions.IFunction2;

public class SoftwareFmCompositeUnit {

	static class Holder extends HasComposite implements IHasSelectionBindingManager {

		private final ISelectedBindingManager<?> bindingManager;
		private TabFolder tabFolder;

		public Holder(Composite parent, ISelectedBindingManager<?> bindingManager) {
			super(parent);
			this.bindingManager = bindingManager;
		}

		@Override
		protected Composite makeComposite(Composite parent, ImageRegistry imageRegistry) {
			return tabFolder = new TabFolder(parent, SWT.NULL);
		}

		@SuppressWarnings("unchecked")
		public <S> ISelectedBindingManager<S> getBindingManager() {
			return (ISelectedBindingManager<S>) bindingManager;
		}

		public TabFolder getTabFolder() {
			return tabFolder;
		}

	}

	public SoftwareFmCompositeUnit(String title, final IFunction2<Composite, SoftwareFmContainer<Map<String, Object>>, SoftwareFmComposite>... creators) {
		final ExecutorService threadingPool = Executors.newCachedThreadPool();
		try {
			Swts.Show.xUnit(title, new File("src/test/resources/org/softwarefm/eclipse/composite"), "dat", new ISituationListAndBuilder<Holder, String>() {
				public Holder makeChild(Composite parent) throws Exception {
					SoftwareFmContainer<Map<String, Object>> container = makeContainer(threadingPool, parent.getDisplay());
					Holder holder = new Holder(parent, container.selectedBindingManager);
					for (IFunction2<Composite, SoftwareFmContainer<Map<String, Object>>, SoftwareFmComposite> creator : creators) {
						SoftwareFmComposite softwareFmComposite = Functions.call(creator, holder.getComposite(), container);
						TabItem tabItem = new TabItem(holder.getTabFolder(), SWT.NULL);
						tabItem.setText(softwareFmComposite.getClass().getSimpleName());
						tabItem.setControl(softwareFmComposite.getComposite());
					}
					return holder;
				}

				@SuppressWarnings("unchecked")
				public void selected(Holder control, String context, String value) throws Exception {
					ISelectedBindingManager<Map<String, Object>> selectionBindingManager = control.getBindingManager();
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

	public static final IFunction2<Composite, SoftwareFmContainer<Map<String, Object>>, SoftwareFmComposite> allCreator = new IFunction2<Composite, SoftwareFmContainer<Map<String, Object>>, SoftwareFmComposite>() {
		public SoftwareFmComposite apply(Composite parent, SoftwareFmContainer<Map<String, Object>> container) throws Exception {
			return new AllComposite(parent, container);
		}
	};
	public static final IFunction2<Composite, SoftwareFmContainer<Map<String, Object>>, SoftwareFmComposite> classAndMethodCreator = new IFunction2<Composite, SoftwareFmContainer<Map<String, Object>>, SoftwareFmComposite>() {
		public SoftwareFmComposite apply(Composite parent, SoftwareFmContainer<Map<String, Object>> container) throws Exception {
			return new CodeComposite(parent, container);
		}
	};
	public static final IFunction2<Composite, SoftwareFmContainer<Map<String, Object>>, SoftwareFmComposite> projectCreator = new IFunction2<Composite, SoftwareFmContainer<Map<String, Object>>, SoftwareFmComposite>() {
		public SoftwareFmComposite apply(Composite parent, SoftwareFmContainer<Map<String, Object>> container) throws Exception {
			return new ArtifactComposite(parent, container);
		}
	};
	public static final IFunction2<Composite, SoftwareFmContainer<Map<String, Object>>, SoftwareFmComposite> manualImportCreator = new IFunction2<Composite, SoftwareFmContainer<Map<String, Object>>, SoftwareFmComposite>() {
		public SoftwareFmComposite apply(Composite parent, SoftwareFmContainer<Map<String, Object>> container) throws Exception {
			return new LinkComposite(parent, container);
		}
	};
	public static final IFunction2<Composite, SoftwareFmContainer<Map<String, Object>>, SoftwareFmComposite> mavenImportCreator = new IFunction2<Composite, SoftwareFmContainer<Map<String, Object>>, SoftwareFmComposite>() {
		public SoftwareFmComposite apply(Composite parent, SoftwareFmContainer<Map<String, Object>> container) throws Exception {
			return new MavenImportComposite(parent, container);
		}
	};
	public static final IFunction2<Composite, SoftwareFmContainer<Map<String, Object>>, SoftwareFmComposite> debugCreator = new IFunction2<Composite, SoftwareFmContainer<Map<String, Object>>, SoftwareFmComposite>() {
		public SoftwareFmComposite apply(Composite parent, SoftwareFmContainer<Map<String, Object>> container) throws Exception {
			return new DebugTextComposite(parent, container);
		}
	};

	@SuppressWarnings("unchecked")
	public static IFunction2<Composite, SoftwareFmContainer<Map<String, Object>>, SoftwareFmComposite>[] creators = new IFunction2[] { allCreator, classAndMethodCreator, projectCreator, manualImportCreator, mavenImportCreator, debugCreator };

	@SuppressWarnings({ "unused" })
	public static void main(String[] args) {
		new SoftwareFmCompositeUnit(SoftwareFmCompositeUnit.class.getName(), creators);
	}

	public static SoftwareFmContainer<Map<String, Object>> makeContainer(final ExecutorService threadingPool, Display display) {
		final SwtThreadSelectedBindingAggregator<Map<String, Object>> listenerManager = new SwtThreadSelectedBindingAggregator<Map<String, Object>>(new Shell().getDisplay());
		IArtifactDataCache artifactDataCache = IArtifactDataCache.Utils.artifactDataCache();
		SelectedArtifactSelectionManager<Map<String, Object>, Map<String, Object>> manager = new SelectedArtifactSelectionManager<Map<String, Object>, Map<String, Object>>(//
				listenerManager, //
				ISelectedBindingStrategy.Utils.fromMap(), //
				threadingPool, //
				artifactDataCache,//
				ICallback.Utils.rethrow());
		IUrlStrategy urlStrategy = IUrlStrategy.Utils.urlStrategy();
		ITemplateStore templateStore = ITemplateStore.Utils.templateStore(urlStrategy);
		final IMakeLink makeLink = IMakeLink.Utils.makeLink(urlStrategy, templateStore, artifactDataCache);
		ImageRegistry imageRegistry = new ImageRegistry(display);
		ImageConstants.initializeImageRegistry(display, imageRegistry);
		SoftwareFmContainer<Map<String, Object>> container = SoftwareFmContainer.make(urlStrategy, //
				manager, //
				IMaven.Utils.importPomWithSysouts(makeLink, manager),//
				IMakeLink.Utils.manuallyImportWhenNotInEclipse(makeLink, manager),//
				templateStore, //
				artifactDataCache, //
				new SfmActionState(),//
				imageRegistry);
		return container;
	}
}