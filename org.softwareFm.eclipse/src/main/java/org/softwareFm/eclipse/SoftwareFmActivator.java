package org.softwareFm.eclipse;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;
import org.softwareFm.configuration.SoftwareFmPropertyAnchor;
import org.softwareFm.display.browser.IBrowserConfigurator;
import org.softwareFm.explorer.eclipse.ISelectedBindingManager;
import org.softwareFm.explorer.eclipse.Plugins;
import org.softwareFm.explorer.eclipse.SelectedArtifactSelectionManager;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.constants.HttpClientConstants;
import org.softwareFm.jdtBinding.api.IBindingRipper;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.repositoryFacard.impl.RepositoryFacard;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.services.IServiceExecutor;

/**
 * The activator class controls the plug-in life cycle
 */
public class SoftwareFmActivator extends AbstractUIPlugin {
	public static String dataStoreConfiguratorId = "org.softwareFm.eclipse.dataStore";
	public static String smallButtonStoreConfiguratorId = "org.softwareFm.eclipse.smallButton";
	public static String actionStoreConfiguratorId = "org.softwareFm.eclipse.action";
	public static String displayerStoreConfiguratorId = "org.softwareFm.eclipse.displayer";
	public static String listEditorStoreConfiguratorId = "org.softwareFm.eclipse.listEditor";
	public static String editorConfiguratorId = "org.softwareFm.eclipse.editor";
	public static String largeButtonConfiguratorId = "org.softwareFm.eclipse.largeButton";
	private static final String browserConfigurationId = "org.softwareFm.eclipse.browserConfiguration";

	// The plug-in ID
	public static final String PLUGIN_ID = "org.softwareFm.eclipse.eclipse";
	// The shared instance
	private static SoftwareFmActivator plugin;

	private SelectedArtifactSelectionManager selectedArtifactSelectionManager;
	IRepositoryFacard repository;
	private IResourceGetter resourceGetter;
	private String uuid;
	private IServiceExecutor service;

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
		super.stop(context);
		if (selectedArtifactSelectionManager != null) {
			Plugins.walkSelectionServices(new ICallback<ISelectionService>() {
				@Override
				public void process(ISelectionService t) throws Exception {
					t.removePostSelectionListener(selectedArtifactSelectionManager);
				}
			});
		}
		if (repository != null)
			repository.shutdown();
		repository = null;
		if (service != null)
			service.shutdown();
		service = null;
	}

	public String getUuid() {
		return uuid == null ? uuid = findOrMakeUuid() : uuid;
	}

	private String findOrMakeUuid() {
		try {
			@SuppressWarnings("deprecation")
			IEclipsePreferences prefs = new InstanceScope().getNode(PLUGIN_ID);
			String uuid = prefs.get("Uuid", null);
			if (uuid == null) {
				uuid = UUID.randomUUID().toString();
				prefs.put("Uuid", uuid);
				prefs.flush();
			}
			return uuid;
		} catch (BackingStoreException e) {
			throw WrappedException.wrap(e);
		}
	}

	public static SoftwareFmActivator getDefault() {
		return plugin;
	}

	public ICallback<Throwable> onException() {
		return ICallback.Utils.sysErrCallback();
	}

	public IResourceGetter getResourceGetter() {
		return resourceGetter == null ? resourceGetter = IResourceGetter.Utils.noResources().with(SoftwareFmPropertyAnchor.class, "SoftwareFmDisplay") : resourceGetter;
	}


	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public ISelectedBindingManager getSelectedBindingManager() {
		return selectedArtifactSelectionManager == null ? selectedArtifactSelectionManager = makeNewSelectedArtifactManager() : selectedArtifactSelectionManager;

	}

	private SelectedArtifactSelectionManager makeNewSelectedArtifactManager() {
		final SelectedArtifactSelectionManager result = new SelectedArtifactSelectionManager(IBindingRipper.Utils.ripper());
		Plugins.walkSelectionServices(new ICallback<ISelectionService>() {
			@Override
			public void process(ISelectionService t) throws Exception {
				t.addPostSelectionListener(result);
			}
		});
		return result;
	}

	public IRepositoryFacard getRepository() {
		if (repository == null) {
			// String host = HttpClientConstants.defaultHost;// "178.79.180.172";
			// int port = HttpClientConstants.defaultPort;// 8080;
			String host = "178.79.180.172";
			int port = 8080;
			IHttpClient client = IHttpClient.Utils.builder(host, port).withCredentials(HttpClientConstants.userName, HttpClientConstants.password).//
					setDefaultHeaders(Arrays.<NameValuePair> asList(new BasicNameValuePair("SoftwareFm", getUuid())));
			repository = new RepositoryFacard(client, "sfm");
		}
		return repository;
	}
	

//	public SoftwareFmDataComposite makeComposite(final Composite parent) {
//		Display display = parent.getDisplay();
//		final GuiDataStore guiDataStore = getGuiDataStore();
//		getSelectedBindingManager().addSelectedArtifactSelectionListener(new ISelectedBindingListener() {
//			@Override
//			public void selectionOccured(final BindingRipperResult rippedResult) {
//				getEditorFactory(parent.getDisplay()).cancel();
//				IPath path = rippedResult.path;
//				if (path != null) {
//					final Map<String, Object> context = Maps.<String, Object> newMap();
//					final RippedResult result = makeRippedResult(guiDataStore, rippedResult, context);
//					getEditorFactory(parent.getDisplay()).cancel();
//					guiDataStore.processData(ConfigurationConstants.primaryEntity, result, context);
//				}
//			}
//
//			private RippedResult makeRippedResult(final GuiDataStore guiDataStore, final BindingRipperResult rippedResult, final Map<String, Object> context) {
//				IPath path = rippedResult.path;
//				String javadoc = JavaProjects.findJavadocFor(rippedResult.classpathEntry);
//				String source = JavaProjects.findSourceFor(rippedResult.packageFragmentRoot);
//				String name = path.lastSegment().toString();
//				String extension = Files.extension(name);
//				String hexDigest = extension.equals("jar") ? rippedResult.hexDigest : null;
//				String javaProject = rippedResult.javaProject == null ? null : rippedResult.javaProject.getElementName();
//				final RippedResult result = new RippedResult(hexDigest, javaProject, path.toOSString(), name, javadoc, source, null, null);
//				IJavadocSourceMutator sourceMutator = new IJavadocSourceMutator() {
//					@Override
//					public void setNewValue(final String newValue, final IJavadocSourceMutatorCallback whenComplete) throws Exception {
//						final BindingRipperResult reripped = SelectedArtifactSelectionManager.reRip(rippedResult);
//						getExecutorService().submit(new Callable<Void>() {
//							@Override
//							public Void call() throws Exception {
//								try {
//									if (newValue.endsWith(".jar")) {
//										File file = Files.downloadAndPutIndirectory(newValue, ConfigurationConstants.defaultDirectoryForDownloads);
//										JavaProjects.setSourceAttachment(reripped.javaProject, reripped.classpathEntry, file.getCanonicalPath());
//									} else
//										JavaProjects.setSourceAttachment(reripped.javaProject, reripped.classpathEntry, newValue);
//									whenComplete.process(newValue, newValue);
//								} catch (IOException e) {
//									e.printStackTrace();
//									throw WrappedException.wrap(e);
//								}
//								return null;
//							}
//						});
//					}
//				};
//				IJavadocSourceMutator javadocMutator = new IJavadocSourceMutator() {
//					@Override
//					public void setNewValue(final String newValue, final IJavadocSourceMutatorCallback whenComplete) throws Exception {
//						final BindingRipperResult reripped = SelectedArtifactSelectionManager.reRip(rippedResult);
//						getExecutorService().submit(new  Callable<Void>() {
//							@Override
//							public Void call() {
//								try {
//									if (newValue.endsWith(".jar")) {
//										File file = Files.downloadAndPutIndirectory(newValue, ConfigurationConstants.defaultDirectoryForDownloads);
//										JavaProjects.setJavadoc(reripped.javaProject, reripped.classpathEntry, "jar:file:" + file.getCanonicalPath() + "!/");
//									} else
//										JavaProjects.setJavadoc(reripped.javaProject, reripped.classpathEntry, newValue);
//									whenComplete.process(newValue, newValue);
//								} catch (IOException e) {
//									e.printStackTrace();
//									throw WrappedException.wrap(e);
//								}
//								return null;
//							}
//						});
//					}
//				};
//				result.put(JdtConstants.javadocMutatorKey, javadocMutator);
//				result.put(JdtConstants.sourceMutatorKey, sourceMutator);
//				return result;
//			}
//		});
//		List<IBrowserConfigurator> browserConfigurators = getBrowserConfigurators();
//		IPlayListGetter playListGetter = new PlayListFromArtifactGetter(guiDataStore, ConfigurationConstants.entityForPlayList, Arrays.<IPlayListContributor> asList(new BasicPlaylistContributor()));
//		SoftwareFmDataComposite composite = new SoftwareFmDataComposite(parent, getExecutorService(), guiDataStore, getCompositeConfig(display), getActionStore(), getEditorFactory(display), getUpdateStore(), getListEditorStore(), onException(), getLargeButtonDefns(), browserConfigurators, playListGetter);
//		return composite;
//
//	}

	private IServiceExecutor getExecutorService() {
		return service == null ? service = IServiceExecutor.Utils.defaultExecutor() : service;
	}

	public List<IBrowserConfigurator> getBrowserConfigurators() {
		List<IBrowserConfigurator> browserConfigurators = Plugins.makeListFrom(browserConfigurationId, onException());
		return browserConfigurators;
	}

}
