package org.arc4eclipse.selectedArtifact.plugin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.arc4eclipse.selectedArtefactPlugin"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	public Activator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

}
