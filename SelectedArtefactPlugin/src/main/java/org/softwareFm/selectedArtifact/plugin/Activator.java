package org.softwareFm.selectedArtifact.plugin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.softwareFm.core.plugin.SoftwareFmActivator;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.IDisplayContainer;
import org.softwareFm.panel.ISelectedBindingManager;
import org.softwareFm.repository.api.ISoftwareFmRepository;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.softwareFm.selectedArtefactPlugin"; //$NON-NLS-1$

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

	public DisplayerContext getContext(Composite parent) {
		SoftwareFmActivator coreActivator = SoftwareFmActivator.getDefault();
		return coreActivator.getDisplayerContext(parent);
	}

	public ISoftwareFmRepository getRepository() {
		return SoftwareFmActivator.getDefault().getRepository();
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public ISelectedBindingManager getSelectedBindingManager() {
		return SoftwareFmActivator.getDefault().getSelectedBindingManager();
	}

	public IDisplayContainer makeDisplayContainer(Composite parent, String view, String entity) {
		return SoftwareFmActivator.getDefault().makeDisplayContainer(parent, view, entity);
	}

}
