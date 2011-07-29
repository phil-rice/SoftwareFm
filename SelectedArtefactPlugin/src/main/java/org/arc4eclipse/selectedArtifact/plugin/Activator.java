package org.arc4eclipse.selectedArtifact.plugin;

import java.util.Collections;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.displayCore.api.IDisplayManager;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.jdtBinding.api.IBindingRipper;
import org.arc4eclipse.panel.ISelectedBindingListener;
import org.arc4eclipse.selectedArtifactPlugin.views.SelectedArtifactSelectionManager;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
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

	private IArc4EclipseRepository repository;

	private IFunction1<IBinding, BindingRipperResult> bindingRipper;

	private IDisplayManager displayManager;

	private SelectedArtifactSelectionManager selectedBindingManager;

	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		repository = IArc4EclipseRepository.Utils.repository();
		plugin = this;
		selectedBindingManager = new SelectedArtifactSelectionManager(getBindingRipper());
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow[] workbenchWindows = workbench.getWorkbenchWindows();
		for (int i = 0; i < workbenchWindows.length; i++) {
			IWorkbenchPage page = workbench.getWorkbenchWindows()[i].getActivePage();
			page.addSelectionListener(getSelectedBindingManager());
		}
		selectedBindingManager.addSelectedArtifactSelectionListener(new ISelectedBindingListener() {
			@Override
			public void selectionOccured(BindingRipperResult ripperResult) {
				repository.getJarData(ripperResult.hexDigest, Collections.<String, Object> emptyMap());
			}
		});
	}

	public SelectedArtifactSelectionManager getSelectedBindingManager() {
		return selectedBindingManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		repository.shutdown();
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow[] workbenchWindows = workbench.getWorkbenchWindows();
		for (int i = 0; i < workbenchWindows.length; i++) {
			IWorkbenchPage page = workbench.getWorkbenchWindows()[i].getActivePage();
			page.removeSelectionListener(selectedBindingManager);
		}
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public IArc4EclipseRepository getRepository() {
		return repository;
	}

	public IFunction1<IBinding, BindingRipperResult> getBindingRipper() {
		if (bindingRipper == null)
			bindingRipper = IBindingRipper.Utils.ripper();
		return bindingRipper;
	}

	public IDisplayManager getManager() {
		if (displayManager == null)
			displayManager = IDisplayManager.Utils.displayManager();
		return displayManager;
	}
}
