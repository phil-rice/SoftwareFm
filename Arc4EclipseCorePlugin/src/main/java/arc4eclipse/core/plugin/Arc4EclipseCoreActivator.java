package arc4eclipse.core.plugin;

import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.IStatusChangedListener;
import org.arc4eclipse.arc4eclipseRepository.api.RepositoryDataItemStatus;
import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.api.IDisplayContainerFactory;
import org.arc4eclipse.displayCore.api.IDisplayContainerFactoryBuilder;
import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;
import org.arc4eclipse.displayForums.DisplayForums;
import org.arc4eclipse.displayForums.DisplayTutorials;
import org.arc4eclipse.displayJarPath.DisplayJarPath;
import org.arc4eclipse.displayJavadoc.DisplayJavadoc;
import org.arc4eclipse.displayMailingLists.DisplayMailingLists;
import org.arc4eclipse.displaySource.DisplaySource;
import org.arc4eclipse.displayText.DisplayText;
import org.arc4eclipse.displayUrl.DisplayUrl;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.jdtBinding.api.IBindingRipper;
import org.arc4eclipse.panel.ISelectedBindingListener;
import org.arc4eclipse.swtBasics.images.IImageFactory;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.arc4eclipse.utilities.maps.Maps;
import org.eclipse.jdt.core.dom.ITypeBinding;
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
					Object actualEntity = context.get(RepositoryConstants.entity);
					if (item != null) {
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
							repository.getData(entity, url, Maps.<String, Object> makeMap(RepositoryConstants.entity, entity));
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
				public void selectionOccured(ITypeBinding binding, BindingRipperResult ripperResult) {
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

			builder.registerDisplayer(new DisplayUrl());
			builder.registerDisplayer(new DisplaySource());
			builder.registerDisplayer(new DisplayJavadoc());
			builder.registerDisplayer(new DisplayJarPath());
			builder.registerDisplayer(new DisplayForums());
			builder.registerDisplayer(new DisplayTutorials());
			builder.registerDisplayer(new DisplayMailingLists());

			builder.registerForEntity(RepositoryConstants.entityJarData, RepositoryConstants.jarDetailsKey, RepositoryConstants.jarDetailsTitle, null);
			builder.registerForEntity(RepositoryConstants.entityJarData, RepositoryConstants.sourceKey, RepositoryConstants.sourceTitle, CorePlugInConstants.sourceHelp);
			builder.registerForEntity(RepositoryConstants.entityJarData, RepositoryConstants.javadocKey, RepositoryConstants.javaDocTitle, CorePlugInConstants.javaDocHelp);
			builder.registerForEntity(RepositoryConstants.entityJarData, RepositoryConstants.organisationUrlKey, RepositoryConstants.organisationTitle, CorePlugInConstants.organisationHelp);
			builder.registerForEntity(RepositoryConstants.entityJarData, RepositoryConstants.projectUrlKey, RepositoryConstants.projectTitle, CorePlugInConstants.projectHelp);

			builder.registerForEntity(RepositoryConstants.entityOrganisation, RepositoryConstants.nameKey, RepositoryConstants.nameTitle, CorePlugInConstants.organisationNameHelp);
			builder.registerForEntity(RepositoryConstants.entityOrganisation, RepositoryConstants.descriptionKey, RepositoryConstants.descriptionTitle, CorePlugInConstants.organisationDescriptionHelp);
			builder.registerForEntity(RepositoryConstants.entityOrganisation, RepositoryConstants.issuesKey, RepositoryConstants.issuesTitle, CorePlugInConstants.issuesHelp);
			builder.registerForEntity(RepositoryConstants.entityOrganisation, RepositoryConstants.forumsKey, RepositoryConstants.forumsTitle, CorePlugInConstants.forumsHelp);
			builder.registerForEntity(RepositoryConstants.entityOrganisation, RepositoryConstants.mailingListsKey, RepositoryConstants.mailingListTitle, CorePlugInConstants.organisationMailingListHelp);
			builder.registerForEntity(RepositoryConstants.entityOrganisation, RepositoryConstants.tutorialsKey, RepositoryConstants.tutorialsTitle, CorePlugInConstants.tutorialsHelp);
			builder.registerForEntity(RepositoryConstants.entityOrganisation, RepositoryConstants.projectJobsKey, RepositoryConstants.jobsTitle, CorePlugInConstants.projectJobsHelp);
			builder.registerForEntity(RepositoryConstants.entityOrganisation, RepositoryConstants.merchandisingKey, RepositoryConstants.merchandisingTitle, CorePlugInConstants.merchandisingHelp);

			builder.registerForEntity(RepositoryConstants.entityProject, RepositoryConstants.nameKey, RepositoryConstants.nameTitle, CorePlugInConstants.projectNameHelp);
			builder.registerForEntity(RepositoryConstants.entityProject, RepositoryConstants.descriptionKey, RepositoryConstants.descriptionTitle, CorePlugInConstants.projectDescriptionHelp);
			builder.registerForEntity(RepositoryConstants.entityProject, RepositoryConstants.projectLicenseKey, RepositoryConstants.licenseTitle, CorePlugInConstants.projectLicenseHelp);
			builder.registerForEntity(RepositoryConstants.entityProject, RepositoryConstants.forumsKey, RepositoryConstants.forumsTitle, CorePlugInConstants.forumsHelp);
			builder.registerForEntity(RepositoryConstants.entityProject, RepositoryConstants.mailingListsKey, RepositoryConstants.mailingListTitle, CorePlugInConstants.projectsMailingList);
			builder.registerForEntity(RepositoryConstants.entityProject, RepositoryConstants.merchandisingKey, RepositoryConstants.merchandisingTitle, CorePlugInConstants.merchandisingHelp);

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
