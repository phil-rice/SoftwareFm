package org.arc4eclipse.selectedArtifact.plugin;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.core.plugin.Arc4EclipseCoreActivator;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.IDisplayContainer;
import org.arc4eclipse.panel.ISelectedBindingManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}

	public DisplayerContext getContext(Display display) {
		Arc4EclipseCoreActivator coreActivator = Arc4EclipseCoreActivator.getDefault();
		return new DisplayerContext(coreActivator.getSelectedBindingManager(), coreActivator.getRepository(), coreActivator.getUrlGeneratorMap(), coreActivator.getConfigForTitleAnd(display));
	}

	public IArc4EclipseRepository getRepository() {
		return Arc4EclipseCoreActivator.getDefault().getRepository();
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public ISelectedBindingManager getSelectedBindingManager() {
		return Arc4EclipseCoreActivator.getDefault().getSelectedBindingManager();
	}

	public IDisplayContainer makeDisplayContainer(Composite parent, String entity) {
		return Arc4EclipseCoreActivator.getDefault().makeDisplayContainer(parent, entity);
	}

}
