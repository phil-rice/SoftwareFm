package arc4eclipse.core.plugin;

import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.IStatusChangedListener;
import org.arc4eclipse.arc4eclipseRepository.api.RepositoryDataItemStatus;
import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.api.IDisplayContainerFactory;
import org.arc4eclipse.displayCore.api.IDisplayContainerFactoryBuilder;
import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;
import org.arc4eclipse.displayJarPath.DisplayJarPath;
import org.arc4eclipse.displayJavadoc.DisplayJavadoc;
import org.arc4eclipse.displayOrganisation.DisplayOrganisation;
import org.arc4eclipse.displaySource.DisplaySource;
import org.arc4eclipse.displayText.DisplayText;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.jdtBinding.api.IBindingRipper;
import org.arc4eclipse.panel.ISelectedBindingListener;
import org.arc4eclipse.swtBasics.images.IImageFactory;
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

	private IImageFactory imageFactory;

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

	// TODO This is better done with executable extension points
	public IDisplayContainerFactory getDisplayManager() {
		if (displayContainerFactory == null) {
			IDisplayContainerFactoryBuilder builder = IDisplayContainerFactoryBuilder.Utils.displayManager();
			builder.registerDisplayer(new DisplayText());

			builder.registerDisplayer(new DisplayOrganisation());
			builder.registerDisplayer(new DisplaySource());
			builder.registerDisplayer(new DisplayJavadoc());
			builder.registerDisplayer(new DisplayJarPath());

			builder.registerForEntity(RepositoryConstants.entityJarData, RepositoryConstants.jarPathKey, RepositoryConstants.tutorialsTitle);
			builder.registerForEntity(RepositoryConstants.entityJarData, RepositoryConstants.sourceKey, RepositoryConstants.sourceTitle);
			builder.registerForEntity(RepositoryConstants.entityJarData, RepositoryConstants.javadocKey, RepositoryConstants.javaDocTitle);
			builder.registerForEntity(RepositoryConstants.entityJarData, RepositoryConstants.organisationUrlKey, RepositoryConstants.organisationTitle);
			builder.registerForEntity(RepositoryConstants.entityJarData, RepositoryConstants.projectUrlKey, RepositoryConstants.projectTitle);

			builder.registerForEntity(RepositoryConstants.entityOrganisation, RepositoryConstants.nameKey, RepositoryConstants.nameTitle);
			builder.registerForEntity(RepositoryConstants.entityOrganisation, RepositoryConstants.descriptionKey, RepositoryConstants.descriptionTitle);
			builder.registerForEntity(RepositoryConstants.entityOrganisation, RepositoryConstants.projectJobsKey, RepositoryConstants.jobsTitle);
			builder.registerForEntity(RepositoryConstants.entityOrganisation, RepositoryConstants.merchantisingKey, RepositoryConstants.merchantisingTitle);

			builder.registerForEntity(RepositoryConstants.entityProject, RepositoryConstants.nameKey, RepositoryConstants.nameTitle);
			builder.registerForEntity(RepositoryConstants.entityProject, RepositoryConstants.descriptionKey, RepositoryConstants.descriptionTitle);
			builder.registerForEntity(RepositoryConstants.entityProject, RepositoryConstants.projectLicenseKey, RepositoryConstants.licenseTitle);
			builder.registerForEntity(RepositoryConstants.entityProject, RepositoryConstants.projectMailingListsKey, RepositoryConstants.mailingListTitle);
			builder.registerForEntity(RepositoryConstants.entityProject, RepositoryConstants.projectTutorialsKey, RepositoryConstants.tutorialsTitle);
			builder.registerForEntity(RepositoryConstants.entityProject, RepositoryConstants.projectJobsKey, RepositoryConstants.jobsTitle);
			builder.registerForEntity(RepositoryConstants.entityProject, RepositoryConstants.merchantisingKey, RepositoryConstants.merchantisingTitle);
			displayContainerFactory = builder.build();
		}
		return displayContainerFactory;
	}

	public IImageFactory getImageFactory() {
		if (imageFactory == null)
			imageFactory = IImageFactory.Utils.imageFactory();
		return imageFactory;
	}
}
