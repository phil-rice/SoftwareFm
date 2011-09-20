package org.softwareFm.eclipse;

import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.softwareFm.display.IUrlDataCallback;
import org.softwareFm.display.IUrlToData;
import org.softwareFm.display.data.GuiDataStore;
import org.softwareFm.display.data.IGuiDataStoreConfigurator;
import org.softwareFm.eclipse.plugins.Plugins;
import org.softwareFm.utilities.callbacks.ICallback;

/**
 * The activator class controls the plug-in life cycle
 */
public class SoftwareFmActivator extends AbstractUIPlugin {
	public static String dataStoreConfiguratorId = "org.softwareFm.dataStoreConfigurator";

	// The plug-in ID
	public static final String PLUGIN_ID = "org.softwareFm.eclipse";

	// The shared instance
	private static SoftwareFmActivator plugin;

	private static GuiDataStore guiDataStore;

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
	}

	public static SoftwareFmActivator getDefault() {
		return plugin;
	}

	public ICallback<Throwable> onException() {
		return ICallback.Utils.sysErrCallback();

	}

	public GuiDataStore getGuiDataStore() {
		if (guiDataStore == null) {
			guiDataStore = new GuiDataStore(new IUrlToData() {
				@Override
				public void getData(String entity, String url, Map<String, Object> context, IUrlDataCallback callback) {
				}
			}, onException());
			Plugins.useConfigElements(dataStoreConfiguratorId, new ICallback<IConfigurationElement>(){
				@Override
				public void process(IConfigurationElement t) throws Exception {
					IGuiDataStoreConfigurator configurator = (IGuiDataStoreConfigurator) t.createExecutableExtension("class");
					configurator.process(guiDataStore);
				}}, onException());
		}
		return guiDataStore;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

}
