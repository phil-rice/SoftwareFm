package org.arc4eclipse.core.plugin;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.Callable;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.IStatusChangedListener;
import org.arc4eclipse.arc4eclipseRepository.api.RepositoryDataItemStatus;
import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.IDisplayContainer;
import org.arc4eclipse.displayCore.api.IDisplayContainerFactory;
import org.arc4eclipse.displayCore.api.IDisplayContainerFactoryBuilder;
import org.arc4eclipse.displayCore.api.IDisplayer;
import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.jdtBinding.api.IBindingRipper;
import org.arc4eclipse.panel.ISelectedBindingListener;
import org.arc4eclipse.swtBasics.images.Images;
import org.arc4eclipse.swtBasics.text.ConfigForTitleAnd;
import org.arc4eclipse.utilities.callbacks.ICallback;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.arc4eclipse.utilities.maps.Maps;
import org.arc4eclipse.utilities.resources.IResourceGetter;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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
	public static final String DISPLAYER_ID = "org.arc4eclipse.displayers";
	public static final String IMAGE_ID = "org.arc4eclipse.image";
	public static final String BUNDLE_ID = "org.arc4eclipse.bundle";

	// The shared instance
	private static Arc4EclipseCoreActivator plugin;

	private IArc4EclipseRepository repository;

	private SelectedArtifactSelectionManager selectedBindingManager;

	private IResourceGetter resourceGetter;

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

	private boolean registeredExtraImages;
	private IDisplayContainerFactoryBuilder displayContainerFactoryBuilder;
	private final Map<String, IDisplayContainerFactory> displayContainerFactoryMap = Maps.newMap();

	@Override
	public ImageRegistry getImageRegistry() {
		throw new IllegalArgumentException("Use with display parameter");
	}

	public ImageRegistry getImageRegistry(final Display display) {
		final ImageRegistry result = super.getImageRegistry();
		if (!registeredExtraImages) {
			Plugins.useConfigElements(IMAGE_ID, new ICallback<IConfigurationElement>() {
				@Override
				public void process(IConfigurationElement t) throws Exception {
					String key = t.getAttribute("key");
					System.out.println("trying to register image " + key);
					if (key == null)
						throw new IllegalArgumentException(MessageFormat.format(DisplayCoreConstants.attributeMissing, key, t.getAttribute("class")));
					Class<Object> clazz = Plugins.classFrom(t);
					Images.registerImages(display, result, clazz, key);
					System.out.println("....registered image " + key);
				}
			}, ICallback.Utils.sysErrCallback());
			registeredExtraImages = true;
		}
		return result;
	}

	public IDisplayContainerFactory getDisplayContainerFactory(final Display display, final String entity) {
		return Maps.findOrCreate(displayContainerFactoryMap, entity, new Callable<IDisplayContainerFactory>() {
			@Override
			public IDisplayContainerFactory call() throws Exception {
				IDisplayContainerFactoryBuilder builder = getDisplayContainerFactoryBuilder(display);
				IDisplayContainerFactory factory = builder.build(entity);
				if (entity.equals(RepositoryConstants.entityJarData)) {
					factory.register(makeMap("jarData", "jar", "Jar"));
					factory.register(makeMap("javadoc", "javadoc", "Javadoc"));
					factory.register(makeMap("source", "src", "Source"));
					factory.register(makeMap("project.url", "url"));
					factory.register(makeMap("organisation.url", "url"));
				} else if (entity.equals(RepositoryConstants.entityProject)) {
					factory.register(makeMap("project.url", "url"));
					factory.register(makeMap("project.name", "text"));
					factory.register(makeMap("mailingLists", "list"));
					factory.register(makeMap("tutorials", "list"));
				} else if (entity.equals(RepositoryConstants.entityOrganisation)) {
					factory.register(makeMap("organisation.url", "url"));
					factory.register(makeMap("organisation.name", "text"));
				}
				return factory;
			}

			private Map<String, String> makeMap(String key, String displayer) {
				return makeMap(key, displayer, "Clear");
			}

			private Map<String, String> makeMap(String key, String displayer, String smallImageKey) {
				return Maps.<String, String> makeLinkedMap(DisplayCoreConstants.key, key, DisplayCoreConstants.displayer, displayer, DisplayCoreConstants.smallImageKey, smallImageKey);
			}
		});
	}

	public IDisplayContainer makeDisplayContainer(Composite parent, String entity) {
		Display display = parent.getDisplay();
		IDisplayContainerFactory factory = getDisplayContainerFactory(display, entity);
		return factory.create(getDisplayerContext(display), parent);
	}

	private IDisplayContainerFactoryBuilder getDisplayContainerFactoryBuilder(final Display display) {
		if (displayContainerFactoryBuilder == null) {
			displayContainerFactoryBuilder = IDisplayContainerFactoryBuilder.Utils.factoryBuilder();
			Plugins.useClasses(DISPLAYER_ID, new PlugInSysErrAdapter<IDisplayer<?, ?>>() {
				@Override
				public void process(IDisplayer<?, ?> t, IConfigurationElement element) {
					String key = element.getAttribute(DisplayCoreConstants.key);
					String clazz = element.getAttribute(DisplayCoreConstants.clazz);
					if (key == null || clazz == null)
						throw new NullPointerException(MessageFormat.format(DisplayCoreConstants.attributeMissing, key, clazz));
					System.out.println("Registering Displayer " + key + ", " + t);
					displayContainerFactoryBuilder.registerDisplayer(key, t);
				}
			});
		}
		return displayContainerFactoryBuilder;
	}

	private DisplayerContext getDisplayerContext(Display display) {
		return new DisplayerContext(getSelectedBindingManager(), getRepository(), getConfigForTitleAnd(display));
	}

	public IResourceGetter getResourceGetter() {
		if (resourceGetter == null) {
			resourceGetter = IResourceGetter.Utils.noResources();
			Plugins.useConfigElements(BUNDLE_ID, new ICallback<IConfigurationElement>() {
				@Override
				public void process(IConfigurationElement t) throws Exception {
					Class<? extends Object> anchorClass = t.createExecutableExtension("class").getClass();
					String name = t.getAttribute("name");
					if (name == null)
						throw new RuntimeException(MessageFormat.format(DisplayCoreConstants.nameMissing, anchorClass));
					resourceGetter = resourceGetter.with(anchorClass, name);
				}
			}, ICallback.Utils.sysErrCallback());
		}
		return resourceGetter;
	}

	public void clear() {
		final ImageRegistry imageRegistry = super.getImageRegistry();
		Plugins.useConfigElements(IMAGE_ID, new ICallback<IConfigurationElement>() {
			@Override
			public void process(IConfigurationElement t) throws Exception {
				String key = t.getAttribute("key");
				Images.removeImages(imageRegistry, key);
			}
		}, ICallback.Utils.sysErrCallback());
		registeredExtraImages = false;

	}

	public ConfigForTitleAnd getConfigForTitleAnd(Display display) {
		return ConfigForTitleAnd.create(display, getResourceGetter(), getImageRegistry(display));
	}

}
