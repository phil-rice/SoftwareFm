package org.softwarefm.core.composite;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.link.IMakeLink;
import org.softwarefm.core.maven.IMaven;
import org.softwarefm.core.selection.IHasSelectionBindingManager;
import org.softwarefm.core.selection.ISelectedBindingManager;
import org.softwarefm.core.selection.ISelectedBindingStrategy;
import org.softwarefm.core.selection.internal.SelectedArtifactSelectionManager;
import org.softwarefm.core.selection.internal.SwtThreadSelectedBindingAggregator;
import org.softwarefm.core.swt.HasComposite;
import org.softwarefm.core.swt.ISituationListAndBuilder;
import org.softwarefm.core.swt.Swts;
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
		protected Composite makeComposite(Composite parent) {
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
					final SwtThreadSelectedBindingAggregator<Map<String, Object>> listenerManager = new SwtThreadSelectedBindingAggregator<Map<String, Object>>(new Shell().getDisplay());
					SelectedArtifactSelectionManager<Map<String, Object>, Map<String, Object>> manager = new SelectedArtifactSelectionManager<Map<String, Object>, Map<String, Object>>(//
							listenerManager, //
							ISelectedBindingStrategy.Utils.fromMap(), //
							threadingPool, //
							ICallback.Utils.rethrow());
					SoftwareFmContainer<Map<String, Object>> container = SoftwareFmContainer.make(manager, //
							IMaven.Utils.importPomWithSysouts(IMakeLink.Utils.makeLink()), //
							ICallback.Utils.<ProjectData> exception(""));
					Holder holder = new Holder(parent, manager);
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
			return new ClassAndMethodComposite(parent, container);
		}
	};
	public static final IFunction2<Composite, SoftwareFmContainer<Map<String, Object>>, SoftwareFmComposite> digestCreator = new IFunction2<Composite, SoftwareFmContainer<Map<String, Object>>, SoftwareFmComposite>() {
		public SoftwareFmComposite apply(Composite parent, SoftwareFmContainer<Map<String, Object>> container) throws Exception {
			return new DigestComposite(parent, container);
		}
	};
	public static final IFunction2<Composite, SoftwareFmContainer<Map<String, Object>>, SoftwareFmComposite> manualImportCreator = new IFunction2<Composite, SoftwareFmContainer<Map<String, Object>>, SoftwareFmComposite>() {
		public SoftwareFmComposite apply(Composite parent, SoftwareFmContainer<Map<String, Object>> container) throws Exception {
			return new ManualImportComposite(parent, container);
		}
	};
	public static final IFunction2<Composite, SoftwareFmContainer<Map<String, Object>>, SoftwareFmComposite> mavenImportCreator = new IFunction2<Composite, SoftwareFmContainer<Map<String, Object>>, SoftwareFmComposite>() {
		public SoftwareFmComposite apply(Composite parent, SoftwareFmContainer<Map<String, Object>> container) throws Exception {
			return new MavenImportComposite(parent, container);
		}
	};

	@SuppressWarnings({ "unused", "unchecked" })
	public static void main(String[] args) {
		new SoftwareFmCompositeUnit(SoftwareFmCompositeUnit.class.getName(), allCreator, classAndMethodCreator, digestCreator, manualImportCreator, mavenImportCreator);
	}
}
