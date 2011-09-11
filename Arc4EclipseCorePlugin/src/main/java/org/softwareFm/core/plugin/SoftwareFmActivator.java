package org.softwareFm.core.plugin;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
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
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.IDisplayContainer;
import org.softwareFm.displayCore.api.IDisplayContainerButtons.Utils;
import org.softwareFm.displayCore.api.IDisplayContainerFactory;
import org.softwareFm.displayCore.api.IDisplayContainerFactoryBuilder;
import org.softwareFm.displayCore.api.IDisplayContainerFactoryGetter;
import org.softwareFm.displayCore.api.IDisplayer;
import org.softwareFm.displayCore.api.ILineEditor;
import org.softwareFm.displayCore.api.IRepositoryAndUrlGeneratorMapGetter;
import org.softwareFm.displayCore.api.RepositoryStatusListenerPropogator;
import org.softwareFm.displayCore.constants.DisplayCoreConstants;
import org.softwareFm.jdtBinding.api.BindingRipperResult;
import org.softwareFm.jdtBinding.api.IBindingRipper;
import org.softwareFm.panel.ISelectedBindingListener;
import org.softwareFm.repository.api.ISoftwareFmRepository;
import org.softwareFm.repository.api.IUrlGenerator;
import org.softwareFm.repository.api.IUrlGeneratorMap;
import org.softwareFm.repository.constants.RepositoryConstants;
import org.softwareFm.softwareFmImages.IImageRegister;
import org.softwareFm.swtBasics.images.Images;
import org.softwareFm.swtBasics.text.ConfigForTitleAnd;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

/**
 * The activator class controls the plug-in life cycle
 */
public class SoftwareFmActivator extends AbstractUIPlugin implements IRepositoryAndUrlGeneratorMapGetter, IDisplayContainerFactoryGetter {

	public static final String DISPLAYER_ID = "org.softwareFm.displayers";
	public static final String IMAGE_ID = "org.softwareFm.image";
	public static final String BUNDLE_ID = "org.softwareFm.bundle";
	public static final String URL_GENERATOR_ID = "org.softwareFm.urlGenerator";
	public static final String LINE_EDITOR_ID = "org.softwareFm.lineEditor";
	public static final String REPOSITORY_PROPOGATOR_ID = "org.softwareFm.repositoryPropogator";
	public static final String DISPLAY_CONTAINER_FACTORY_CONFIGURATOR_ID = "org.softwareFm.displayContainerFactoryConfigurator";

	// The shared instance
	private static SoftwareFmActivator plugin;

	private ISoftwareFmRepository repository;

	private SelectedArtifactSelectionManager selectedBindingManager;

	private IResourceGetter resourceGetter;
	private IUrlGeneratorMap generatorMap;

	/**
	 * The constructor
	 */
	public SoftwareFmActivator() {
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

	public static SoftwareFmActivator getDefault() {
		return plugin;
	}

	@Override
	public IUrlGeneratorMap getUrlGeneratorMap() {
		if (generatorMap == null) {
			final Map<String, IUrlGenerator> map = Maps.newMap();
			Plugins.useClasses(URL_GENERATOR_ID, new IPlugInCreationCallback<IUrlGenerator>() {

				@Override
				public void process(IUrlGenerator t, IConfigurationElement element) {
					String name = element.getAttribute(CorePlugInConstants.urlGeneratorName);
					map.put(name, t);
				}

				@Override
				public void onException(Throwable throwable, IConfigurationElement element) {
					throwable.printStackTrace();
				}
			});
			generatorMap = IUrlGeneratorMap.Utils.urlGeneratorMap(map);
		}
		return generatorMap;
	}

	@Override
	public ISoftwareFmRepository getRepository() {
		if (repository == null) {
			repository = ISoftwareFmRepository.Utils.repository();
			addRespositoryStatusPropogators(repository);
		}
		return repository;
	}

	private void addRespositoryStatusPropogators(final ISoftwareFmRepository repository2) {
		Plugins.useClasses(REPOSITORY_PROPOGATOR_ID, new IPlugInCreationCallback<RepositoryStatusListenerPropogator>() {

			@Override
			public void process(RepositoryStatusListenerPropogator t, IConfigurationElement element) {
				t.setRepositoryAndUrlGeneratorMapGetter(SoftwareFmActivator.this);
				repository2.addStatusListener(t);
			}

			@Override
			public void onException(Throwable throwable, IConfigurationElement element) {
				throwable.printStackTrace();
			}
		});
	}

	public SelectedArtifactSelectionManager getSelectedBindingManager() {
		if (selectedBindingManager == null) {
			selectedBindingManager = new SelectedArtifactSelectionManager(IBindingRipper.Utils.ripper());
			selectedBindingManager.addSelectedArtifactSelectionListener(new ISelectedBindingListener() {
				@Override
				public void selectionOccured(BindingRipperResult ripperResult) {
					Map<String, Object> context = Maps.makeMap(DisplayCoreConstants.ripperResult, ripperResult);
					IUrlGeneratorMap urlGeneratorMap = getUrlGeneratorMap();
					IUrlGenerator urlGenerator = urlGeneratorMap.get(RepositoryConstants.entityJar);
					String hexDigest = ripperResult.hexDigest;
					String jarUrl = urlGenerator.apply(hexDigest);
					getRepository().getData(RepositoryConstants.entityJar, jarUrl, context);
				}
			});
			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow[] workbenchWindows = workbench.getWorkbenchWindows();
			for (int i = 0; i < workbenchWindows.length; i++) {
				IWorkbenchWindow workbenchWindow = workbench.getWorkbenchWindows()[i];
				ISelectionService selectionService = workbenchWindow.getSelectionService();
				selectionService.addPostSelectionListener(selectedBindingManager);
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
					Class<IImageRegister> clazz = Plugins.classFrom(t);
					IImageRegister register = clazz.newInstance();
					register.registerWith(display, result);
				}
			}, ICallback.Utils.sysErrCallback());
			registeredExtraImages = true;
		}
		return result;
	}

	@Override
	public IDisplayContainerFactory getDisplayContainerFactory(final Display display, final String viewName) {
		return Maps.findOrCreate(displayContainerFactoryMap, viewName, new Callable<IDisplayContainerFactory>() {
			@Override
			public IDisplayContainerFactory call() throws Exception {
				IDisplayContainerFactoryBuilder builder = getDisplayContainerFactoryBuilder(display);
				final IDisplayContainerFactory factory = builder.build();
				Plugins.useClasses(DISPLAY_CONTAINER_FACTORY_CONFIGURATOR_ID, new IPlugInCreationCallback<IDisplayContainerFactoryConfigurer>() {
					@Override
					public void process(IDisplayContainerFactoryConfigurer t, IConfigurationElement element) {
						String thisViewName = element.getAttribute("name");
						if (thisViewName == null)
							throw new IllegalArgumentException(MessageFormat.format(DisplayCoreConstants.attributeMissing, "name", t.getClass()));
						if (viewName.equals(thisViewName))
							t.configure(factory);
					}

					@Override
					public void onException(Throwable throwable, IConfigurationElement element) {
						throwable.printStackTrace();
					}
				});
				return factory;
			}

		});
	}

	public IDisplayContainer makeDisplayContainer(Composite parent, String name, String entity) {
		Display display = parent.getDisplay();
		IDisplayContainerFactory factory = getDisplayContainerFactory(display, name);
		return factory.create(getDisplayerContext(parent), parent);
	}

	@SuppressWarnings("rawtypes")
	private IDisplayContainerFactoryBuilder getDisplayContainerFactoryBuilder(final Display display) {
		if (displayContainerFactoryBuilder == null) {
			displayContainerFactoryBuilder = IDisplayContainerFactoryBuilder.Utils.factoryBuilder(this);
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
			Plugins.useClasses(LINE_EDITOR_ID, new IPlugInCreationCallback<ILineEditor>() {
				@Override
				public void process(ILineEditor t, IConfigurationElement element) throws CoreException {
					ILineEditor editor = (ILineEditor) element.createExecutableExtension("class");
					String name = element.getAttribute("name");
					displayContainerFactoryBuilder.registerLineEditor(name, editor);
				}

				@Override
				public void onException(Throwable throwable, IConfigurationElement element) {
					throwable.printStackTrace();
				}
			});
		}
		return displayContainerFactoryBuilder;
	}

	public DisplayerContext getDisplayerContext(Composite from) {
		ConfigForTitleAnd config = getConfigForTitleAnd(from.getDisplay());
		return new DisplayerContext(getSelectedBindingManager(), getRepository(), getUrlGeneratorMap(), config, Utils.makeButtons(from, config.imageRegistry, config.resourceGetter));
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
				String key = t.getAttribute("editKey");
				Images.removeImage(imageRegistry, key);
			}
		}, ICallback.Utils.sysErrCallback());
		registeredExtraImages = false;

	}

	public ConfigForTitleAnd getConfigForTitleAnd(Display display) {
		return ConfigForTitleAnd.create(display, getResourceGetter(), getImageRegistry(display));
	}

	public static void createForTest() {
		SoftwareFmActivator.plugin = new SoftwareFmActivator();
	}
}
