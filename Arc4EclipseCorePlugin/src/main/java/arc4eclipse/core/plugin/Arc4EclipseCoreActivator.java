package arc4eclipse.core.plugin;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.api.IDisplayManager;
import org.arc4eclipse.displayCore.api.IModifiesToBeDisplayed;
import org.arc4eclipse.displayCore.api.NameSpaceNameAndValue;
import org.arc4eclipse.displayText.DisplayOrganisation;
import org.arc4eclipse.displayText.DisplayText;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.jdtBinding.api.IBindingRipper;
import org.arc4eclipse.panel.ISelectedBindingListener;
import org.arc4eclipse.utilities.collections.Lists;
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
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		if (repository != null)
			repository.shutdown();
		if (selectedBindingManager != null) {
			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow[] workbenchWindows = workbench.getWorkbenchWindows();
			for (int i = 0; i < workbenchWindows.length; i++) {
				IWorkbenchPage page = workbench.getWorkbenchWindows()[i].getActivePage();
				page.removeSelectionListener(selectedBindingManager);
			}
		}
		super.stop(context);
	}

	public static Arc4EclipseCoreActivator getDefault() {
		return plugin;
	}

	public IArc4EclipseRepository getRepository() {
		if (repository == null)
			repository = IArc4EclipseRepository.Utils.repository();
		return repository;
	}

	public SelectedArtifactSelectionManager getSelectedBindingManager() {
		if (selectedBindingManager == null) {
			selectedBindingManager = new SelectedArtifactSelectionManager(IBindingRipper.Utils.ripper());
			selectedBindingManager.addSelectedArtifactSelectionListener(new ISelectedBindingListener() {
				@Override
				public void selectionOccured(BindingRipperResult ripperResult) {
					getRepository().getJarData(ripperResult.hexDigest, Collections.<String, Object> emptyMap());
				}
			});
			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow[] workbenchWindows = workbench.getWorkbenchWindows();
			for (int i = 0; i < workbenchWindows.length; i++) {
				IWorkbenchWindow workbenchWindow = workbench.getWorkbenchWindows()[i];
				ISelectionService selectionService = workbenchWindow.getSelectionService();
				selectionService.addSelectionListener(selectedBindingManager);
			}
		}
		return selectedBindingManager;
	}

	public IDisplayManager getDisplayManager() {
		if (displayManager == null) {
			displayManager = IDisplayManager.Utils.displayManager();
			displayManager.addModifier(new IModifiesToBeDisplayed() {
				@Override
				public List<NameSpaceNameAndValue> add(Map<String, Object> data, Map<String, Object> context) {
					List<NameSpaceNameAndValue> result = Lists.newList();
					Object entity = context.get(RepositoryConstants.entity);
					if (RepositoryConstants.entityJarData.equals(entity)) {
						addDefault(result, data, RepositoryConstants.organisationUrlKey, "<org>");
						addDefault(result, data, RepositoryConstants.projectUrlKey, "<proj>");
					}
					return result;
				}

				private void addDefault(List<NameSpaceNameAndValue> result, Map<String, Object> data, String key, Object defaultValue) {
					Object existing = data == null ? null : data.get(key);
					if (existing == null)
						result.add(NameSpaceNameAndValue.Utils.make(key, defaultValue));
				}
			});
			// TODO This is better done with an executable extension point
			displayManager.registerDisplayer(new DisplayText());
			displayManager.registerDisplayer(new DisplayOrganisation());
		}
		return displayManager;
	}
}
