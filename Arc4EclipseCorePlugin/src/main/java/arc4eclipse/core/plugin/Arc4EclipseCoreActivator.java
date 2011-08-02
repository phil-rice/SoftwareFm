package arc4eclipse.core.plugin;

import java.util.List;
import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.IStatusChangedListener;
import org.arc4eclipse.arc4eclipseRepository.api.RepositoryDataItemStatus;
import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.api.IDisplayContainerFactory;
import org.arc4eclipse.displayCore.api.IModifiesToBeDisplayed;
import org.arc4eclipse.displayCore.api.NameSpaceNameAndValue;
import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;
import org.arc4eclipse.displayJarPath.DisplayJarPath;
import org.arc4eclipse.displayJavadoc.DisplayJavadoc;
import org.arc4eclipse.displayOrganisation.DisplayOrganisation;
import org.arc4eclipse.displaySource.DisplaySource;
import org.arc4eclipse.displayText.DisplayText;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.jdtBinding.api.IBindingRipper;
import org.arc4eclipse.panel.ISelectedBindingListener;
import org.arc4eclipse.utilities.collections.Lists;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.arc4eclipse.utilities.maps.Maps;
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

	private IDisplayContainerFactory displayContainerFactory;

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
		if (repository == null) {
			repository = IArc4EclipseRepository.Utils.repository();
			repository.addStatusListener(new IStatusChangedListener() {
				@Override
				public void statusChanged(String url, RepositoryDataItemStatus status, Map<String, Object> item, Map<String, Object> context) throws Exception {
					if (item != null) {
						Object actualEntity = context.get(RepositoryConstants.entity);
						if (RepositoryConstants.entityJarData.equals(actualEntity)) {
							getDependantData(item, repository.generator().forOrganisation(), RepositoryConstants.entityOrganisation, RepositoryConstants.organisationUrlKey);
							getDependantData(item, repository.generator().forProject(), RepositoryConstants.entityProject, RepositoryConstants.projectUrlKey);
						}
					}
				}

				private void getDependantData(Map<String, Object> item, IFunction1<String, String> urlConvertor, String entity, String key) {
					try {
						String rawUrl = (String) item.get(key);
						if (rawUrl != null && !rawUrl.equals("")) {
							String url = urlConvertor.apply(rawUrl);
							repository.getData(url, Maps.<String, Object> makeMap(RepositoryConstants.entity, entity));
						}
					} catch (Exception e) {
						throw WrappedException.wrap(e);
					}
				}

				@Override
				public String toString() {
					return "GetOrgProjDataFromJarData";
				}
			});
			// repository.addStatusListener(IStatusChangedListener.Utils.sysout());
		}
		return repository;
	}

	public SelectedArtifactSelectionManager getSelectedBindingManager() {
		if (selectedBindingManager == null) {
			selectedBindingManager = new SelectedArtifactSelectionManager(IBindingRipper.Utils.ripper());
			selectedBindingManager.addSelectedArtifactSelectionListener(new ISelectedBindingListener() {
				@Override
				public void selectionOccured(BindingRipperResult ripperResult) {
					Map<String, Object> context = Maps.makeMap(DisplayCoreConstants.ripperResult, ripperResult);
					getRepository().getJarData(ripperResult.hexDigest, context);
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

	public IDisplayContainerFactory getDisplayManager() {
		if (displayContainerFactory == null) {
			displayContainerFactory = IDisplayContainerFactory.Utils.displayManager();
			displayContainerFactory.addModifier(new IModifiesToBeDisplayed() {
				@Override
				public List<NameSpaceNameAndValue> add(Map<String, Object> data, Map<String, Object> context) {
					List<NameSpaceNameAndValue> result = Lists.newList();
					Object entity = context.get(RepositoryConstants.entity);
					if (RepositoryConstants.entityJarData.equals(entity)) {
						addDefault(result, data, RepositoryConstants.jarPathKey, "<ignored>");
						addDefault(result, data, RepositoryConstants.sourceKey, "");
						addDefault(result, data, RepositoryConstants.javadocKey, "");
						addDefault(result, data, RepositoryConstants.organisationUrlKey, "<org>");
						addDefault(result, data, RepositoryConstants.projectUrlKey, "<proj>");
					}
					if (RepositoryConstants.entityOrganisation.equals(entity)) {
						addDefault(result, data, RepositoryConstants.organisationNameKey, "");
						addDefault(result, data, RepositoryConstants.organisationDescriptionKey, "");
					}
					if (RepositoryConstants.entityProject.equals(entity)) {
						addDefault(result, data, RepositoryConstants.projectUrlKey, "");
						addDefault(result, data, RepositoryConstants.projectNameKey, "");
						addDefault(result, data, RepositoryConstants.projectLicenseKey, "");
						addDefault(result, data, RepositoryConstants.projectDescriptionKey, "");
						addDefault(result, data, RepositoryConstants.projectMailingListsKey, "");
						addDefault(result, data, RepositoryConstants.projectTutorialsKey, "");
						addDefault(result, data, RepositoryConstants.projectJobsKey, "");
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
			displayContainerFactory.registerDisplayer(new DisplayText());
			displayContainerFactory.registerDisplayer(new DisplayOrganisation());
			displayContainerFactory.registerDisplayer(new DisplaySource());
			displayContainerFactory.registerDisplayer(new DisplayJavadoc());
			displayContainerFactory.registerDisplayer(new DisplayJarPath());
		}
		return displayContainerFactory;
	}

}
