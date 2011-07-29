package arc4eclipse.core.plugin;

import java.util.Collections;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.displayCore.api.IDisplayManager;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.jdtBinding.api.IBindingRipper;
import org.arc4eclipse.panel.ISelectedBindingListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Arc4EclipseCoreActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.arc4Eclipse.core"; //$NON-NLS-1$

	// The shared instance
	private static Arc4EclipseCoreActivator plugin;

	private IArc4EclipseRepository repository;

	private SelectedArtifactSelectionManager selectedBindingManager;

	private IDisplayManager displayManager;

	/**
	 * The constructor
	 */
	public Arc4EclipseCoreActivator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		repository = IArc4EclipseRepository.Utils.repository();
		selectedBindingManager = new SelectedArtifactSelectionManager(IBindingRipper.Utils.ripper());
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow[] workbenchWindows = workbench.getWorkbenchWindows();
		for (int i = 0; i < workbenchWindows.length; i++) {
			IWorkbenchWindow workbenchWindow = workbench.getWorkbenchWindows()[i];
			ISelectionService selectionService = workbenchWindow.getSelectionService();
			selectionService.addSelectionListener(selectedBindingManager);
		}
		selectedBindingManager.addSelectedArtifactSelectionListener(new ISelectedBindingListener() {
			@Override
			public void selectionOccured(BindingRipperResult ripperResult) {
				repository.getJarData(ripperResult.hexDigest, Collections.<String, Object> emptyMap());
			}
		});
		displayManager = IDisplayManager.Utils.displayManager();
	}

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

	public static Arc4EclipseCoreActivator getDefault() {
		return plugin;
	}

	public IArc4EclipseRepository getRepository() {
		return repository;
	}

	public SelectedArtifactSelectionManager getSelectedBindingManager() {
		return selectedBindingManager;
	}

	public IDisplayManager getDisplayManager() {
		return displayManager;
	}

}
