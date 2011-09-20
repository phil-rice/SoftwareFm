package org.softwareFm.eclipse;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.softwareFm.display.GuiBuilder;
import org.softwareFm.display.IUrlDataCallback;
import org.softwareFm.display.IUrlToData;
import org.softwareFm.display.SoftwareFmLayout;
import org.softwareFm.display.actions.ActionStore;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.GuiDataStore;
import org.softwareFm.display.data.IGuiDataStoreConfigurator;
import org.softwareFm.display.displayer.DisplayerStore;
import org.softwareFm.display.editor.EditorContext;
import org.softwareFm.display.editor.EditorFactory;
import org.softwareFm.display.editor.IEditorFactory;
import org.softwareFm.display.largeButton.ILargeButtonFactory;
import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.display.lists.ListEditorStore;
import org.softwareFm.display.smallButtons.SmallButtonStore;
import org.softwareFm.eclipse.configurators.ActionStoreConfigurator;
import org.softwareFm.eclipse.configurators.DataStoreConfigurator;
import org.softwareFm.eclipse.configurators.DisplayerStoreConfigurator;
import org.softwareFm.eclipse.configurators.EditorFactoryConfigurator;
import org.softwareFm.eclipse.configurators.ListEditorConfigurator;
import org.softwareFm.eclipse.configurators.SmallButtonConfigurator;
import org.softwareFm.eclipse.plugins.IPlugInCreationCallback;
import org.softwareFm.eclipse.plugins.Plugins;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.resources.IResourceGetter;

/**
 * The activator class controls the plug-in life cycle
 */
public class SoftwareFmActivator extends AbstractUIPlugin {
	public static String dataStoreConfiguratorId = "org.softwareFm.dataStore";
	public static String smallButtonStoreConfiguratorId = "org.softwareFm.smallButton";
	public static String actionStoreConfiguratorId = "org.softwareFm.action";
	public static String displayerStoreConfiguratorId = "org.softwareFm.displayer";
	public static String listEditorStoreConfiguratorId = "org.softwareFm.listEditor";
	public static String editorConfiguratorId = "org.softwareFm.editor";
	public static String largeButtonConfiguratorId = "org.softwareFm.largeButton";

	// The plug-in ID
	public static final String PLUGIN_ID = "org.softwareFm.eclipse";

	// The shared instance
	private static SoftwareFmActivator plugin;

	private CompositeConfig compositeConfig;
	private EditorContext editorContext;
	private GuiBuilder guiBuilder;
	private DisplayerStore displayerStore;
	private ActionStore actionStore;
	private SmallButtonStore smallButtonStore;
	private ListEditorStore listEditorStore;
	private GuiDataStore guiDataStore;
	private IEditorFactory editorFactory;
	private List<LargeButtonDefn> largeButtonDefns;

	public SoftwareFmActivator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		compositeConfig = null;
		editorContext = null;
		guiBuilder = null;
		displayerStore = null;
		actionStore = null;
		smallButtonStore = null;
		listEditorStore = null;
		guiDataStore = null;
	}

	public static SoftwareFmActivator getDefault() {
		return plugin;
	}

	public ICallback<Throwable> onException() {
		return ICallback.Utils.sysErrCallback();
	}

	public CompositeConfig getCompositeConfig(Display display) {
		if (compositeConfig == null) {
			ImageRegistry imageRegistry = getImageRegistry();
			new BasicImageRegisterConfigurator().registerWith(display, imageRegistry);
			IResourceGetter resourceGetter = IResourceGetter.Utils.noResources().with(SoftwareFmPropertyAnchor.class, "SoftwareFmDisplay");
			SoftwareFmLayout layout = new SoftwareFmLayout();
			compositeConfig = new CompositeConfig(display, layout, imageRegistry, resourceGetter);
		}
		return compositeConfig;
	}

	public EditorContext getEditorContext(Display display) {
		return editorContext == null ? new EditorContext(getCompositeConfig(display)) : editorContext;
	}

	public GuiBuilder getGuiBuilder() {
		return guiBuilder == null ? guiBuilder = new GuiBuilder(getSmallButtonStore(), getActionStore(), getDisplayerStore(), getListEditorStore()) : guiBuilder;
	}

	private ListEditorStore getListEditorStore() {
		return listEditorStore == null ? Plugins.configureMainWithCallbacks(new ListEditorStore(), listEditorStoreConfiguratorId, "class", onException()) : listEditorStore;
	}

	private DisplayerStore getDisplayerStore() {
		return displayerStore == null ? displayerStore = Plugins.configureMainWithCallbacks(new DisplayerStore(), displayerStoreConfiguratorId, "class", onException()) : displayerStore;
	}

	private ActionStore getActionStore() {
		return actionStore == null ? actionStore = Plugins.configureMainWithCallbacks(new ActionStore(), actionStoreConfiguratorId, "class", onException()) : actionStore;
	}

	private SmallButtonStore getSmallButtonStore() {
		return smallButtonStore == null ? smallButtonStore = Plugins.configureMainWithCallbacks(new SmallButtonStore(), smallButtonStoreConfiguratorId, "class", onException()) : smallButtonStore;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public GuiDataStore getGuiDataStore() {
		return guiDataStore == null ? guiDataStore = Plugins.configureMainWithCallbacks(new GuiDataStore(new IUrlToData() {
			@Override
			public void getData(String entity, String url, Map<String, Object> context, IUrlDataCallback callback) {
			}
		}, onException()), dataStoreConfiguratorId, "class", onException()) : guiDataStore;
	}

	public IEditorFactory getEditorFactory(Display display) {
		return editorFactory == null ? editorFactory = Plugins.configureMainWithCallbacks(new EditorFactory(getEditorContext(display)), editorConfiguratorId, "class", onException()) : editorFactory;
	}

	public List<LargeButtonDefn> getLargeButtonDefns() {
		if (largeButtonDefns == null) {
			largeButtonDefns = Lists.newList();
			final GuiBuilder guiBuilder = getGuiBuilder();
			Plugins.useClasses(largeButtonConfiguratorId, new IPlugInCreationCallback<ILargeButtonFactory>() {

				@Override
				public void process(ILargeButtonFactory t, IConfigurationElement element) throws Exception {
					largeButtonDefns.add(t.apply(guiBuilder));
				}

				@Override
				public void onException(Throwable throwable, IConfigurationElement element) {
					try {
						SoftwareFmActivator.this.onException().process(throwable);
					} catch (Exception e) {
						throw WrappedException.wrap(e);
					}
				}
			});
		}
		return largeButtonDefns;
	}
}
