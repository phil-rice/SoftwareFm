package org.arc4eclipse.selectedArtifact.plugin;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.displayCore.api.IDisplayManager;
import org.arc4eclipse.panel.ISelectedBindingManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import arc4eclipse.core.plugin.Arc4EclipseCoreActivator;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.arc4eclipse.selectedArtefactPlugin"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private IDisplayManager displayManager;

	private IArc4EclipseRepository repository;

	private ISelectedBindingManager selectedBindingManager;

	public Activator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		displayManager = Arc4EclipseCoreActivator.getDefault().getDisplayManager();
		repository = Arc4EclipseCoreActivator.getDefault().getRepository();
		selectedBindingManager = Arc4EclipseCoreActivator.getDefault().getSelectedBindingManager();
	}

	public IDisplayManager getDisplayManager() {
		return displayManager;
	}

	public IArc4EclipseRepository getRepository() {
		return repository;
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

	public ISelectedBindingManager getSelectedBindingManager() {
		return selectedBindingManager;
	}

}
