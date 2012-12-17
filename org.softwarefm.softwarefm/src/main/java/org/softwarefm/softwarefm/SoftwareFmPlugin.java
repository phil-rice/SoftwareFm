package org.softwarefm.softwarefm;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.softwarefm.eclipse.Marker;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.actions.SfmActionState;
import org.softwarefm.eclipse.cache.IArtifactDataCache;
import org.softwarefm.eclipse.composite.SoftwareFmComposite;
import org.softwarefm.eclipse.constants.ImageConstants;
import org.softwarefm.eclipse.jdtBinding.ArtifactData;
import org.softwarefm.eclipse.jdtBinding.CodeData;
import org.softwarefm.eclipse.link.IMakeLink;
import org.softwarefm.eclipse.maven.IMaven;
import org.softwarefm.eclipse.selection.IArtifactStrategy;
import org.softwarefm.eclipse.selection.ISelectedBindingListenerAndAdderRemover;
import org.softwarefm.eclipse.selection.ISelectedBindingManager;
import org.softwarefm.eclipse.selection.ISelectedBindingStrategy;
import org.softwarefm.eclipse.selection.SelectedBindingAdapter;
import org.softwarefm.eclipse.selection.internal.SelectedArtifactSelectionManager;
import org.softwarefm.eclipse.selection.internal.SoftwareFmArtifactHtmlRipper;
import org.softwarefm.eclipse.selection.internal.SoftwareFmArtifactStrategy;
import org.softwarefm.eclipse.selection.internal.SwtThreadSelectedBindingAggregator;
import org.softwarefm.eclipse.templates.ITemplateStore;
import org.softwarefm.eclipse.url.HostOffsetAndUrl;
import org.softwarefm.eclipse.url.IUrlStrategy;
import org.softwarefm.eclipse.usage.IUsage;
import org.softwarefm.eclipse.usage.IUsageReporter;
import org.softwarefm.eclipse.usage.UsageConstants;
import org.softwarefm.eclipse.usage.internal.Usage;
import org.softwarefm.softwarefm.jobs.ManualImportJob;
import org.softwarefm.softwarefm.jobs.MavenImportJob;
import org.softwarefm.softwarefm.plugins.Plugins;
import org.softwarefm.softwarefm.plugins.WorkbenchWindowListenerManager;
import org.softwarefm.softwarefm.selection.internal.EclipseSelectedBindingStrategy;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.callbacks.ICallback2;
import org.softwarefm.utilities.constants.CommonConstants;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.http.IHttpClient;
import org.softwarefm.utilities.maps.Maps;
import org.softwarefm.utilities.resources.IResourceGetter;

/**
 * The activator class controls the plug-in life cycle
 */
public class SoftwareFmPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = SoftwareFmPlugin.class.getPackage().getName();

	private static SoftwareFmPlugin plugin;

	private static Display displayForTests;
	private final Object lock = new Object();

	private ISelectedBindingManager<ITextSelection> selectionBindingManager;

	private ExecutorService executor;

	private ISelectionListener selectionListener;

	private IResourceGetter resourceGetter;

	private SoftwareFmContainer<ITextSelection> container;

	private IUrlStrategy urlStrategy;

	private IArtifactDataCache artifactDataCache;

	private SfmActionState sfmActionState;

	private Map<IViewPart, List<SoftwareFmComposite>> views;

	private ICallback2<Object, String> logger;

	private IUsage usage;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		views = Maps.newMap();
		new Thread() {
			@Override
			public void run() {
				setName("Initialising Selection Binding Manager");
				getSelectionBindingManager();
			}
		}.start();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		views = null;
		plugin = null;
		super.stop(context);
		dispose();
	}

	public static SoftwareFmPlugin makeActivatorForTests(Display display) {
		displayForTests = display;
		return plugin == null ? plugin = new SoftwareFmPlugin() : plugin;
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		ImageConstants.initializeImageRegistry(getDisplay(), reg);
	}

	public static SoftwareFmPlugin getDefault() {
		return plugin;
	}

	public boolean recordUsage() {
		return getPreferenceStore().getBoolean(UsageConstants.recordUsageKey);
	}

	public void setLogger(ICallback2<Object, String> logger) {
		this.logger = logger;
	}

	public ICallback2<Object, String> getLogger() {
		return new ICallback2<Object, String>() {
			public void process(Object arg0, String arg1) throws Exception {
				if (logger != null)
					logger.process(arg0, arg1);
			}
		};
	}

	protected Display getDisplay() {
		if (displayForTests != null)
			return displayForTests;
		else
			return PlatformUI.getWorkbench().getDisplay();
	}

	public ISelectedBindingManager<ITextSelection> getSelectionBindingManager() {
		return selectionBindingManager == null ? selectionBindingManager = makeSelectionBindingManager() : selectionBindingManager;
	}

	public IUsage getUsage() {
		return usage == null ? usage = makeUsage() : usage;
	}

	private IUsage makeUsage() {
		synchronized (lock) {
			final Usage usage = new Usage();
			final IUsageReporter reporter = IUsageReporter.Utils.reporter();
			Thread thread = new Thread() {
				@Override
				public void run() {
					while (true) {
						try {
							Thread.sleep(UsageConstants.updatePeriod);
							if (recordUsage())
								reporter.report(usage);
						} catch (InterruptedException e) {
							throw WrappedException.wrap(e);
						}
					}
				}
			};
			thread.setName("Usage updater");
			thread.start();
			return usage;
		}
	}

	public void addView(IViewPart part, SoftwareFmComposite composite) {
		Maps.addToList(views, part, composite);
	}

	public void removeView(IViewPart part) {
		views.remove(part);
	}

	public Map<IViewPart, List<SoftwareFmComposite>> getViews() {
		return Collections.unmodifiableMap(views);
	}

	@SuppressWarnings("unused")
	private ISelectedBindingManager<ITextSelection> makeSelectionBindingManager() {
		synchronized (lock) {
			if (selectionBindingManager == null) {
				IUrlStrategy urlStrategy = getUrlStrategy();
				ISelectedBindingListenerAndAdderRemover<ITextSelection> listenerManager = new SwtThreadSelectedBindingAggregator<ITextSelection>(getDisplay());
				IArtifactStrategy<ITextSelection> projectStrategy = new SoftwareFmArtifactStrategy<ITextSelection>(IHttpClient.Utils.builder(), new SoftwareFmArtifactHtmlRipper(), urlStrategy);
				ISelectedBindingStrategy<ITextSelection, Expression> strategy = new EclipseSelectedBindingStrategy(projectStrategy);
				ExecutorService executor = getExecutorService();
				SelectedArtifactSelectionManager<ITextSelection, Expression> selectionBindingManager = new SelectedArtifactSelectionManager<ITextSelection, Expression>(listenerManager, strategy, executor, getArtifactDataCache(), ICallback.Utils.sysErrCallback());
				new WorkbenchWindowListenerManager(selectionBindingManager);
				selectionBindingManager.addSelectedArtifactSelectionListener(new SelectedBindingAdapter() {

					@Override
					public void codeSelectionOccured(CodeData codeData, int selectionCount) {
						if (codeData.className != null) {
							HostOffsetAndUrl url = getContainer().urlStrategy.classAndMethodUrl(codeData);
							IUsage usage = getUsage();
							usage.selected(url.url);
						}
					}

					@Override
					public void artifactDetermined(ArtifactData artifactData, int selectionCount) {
						HostOffsetAndUrl url = container.urlStrategy.versionUrl(artifactData);
						IUsage usage = getUsage();
						usage.selected(url.url);
					}

					@Override
					public boolean invalid() {
						return false;
					}
				});
				return selectionBindingManager;
				//
				// selectionListener = new ISelectionListener() {
				// @Override
				// public void selectionChanged(IWorkbenchPart part, ISelection selection) {
				// System.out.println("Selection occured");
				// if (selection instanceof ITextSelection)
				// selectionBindingManager.selectionOccured((ITextSelection) selection);
				// else
				// selectionBindingManager.selectionOccured(null);
				// }
				// };
				// if (displayForTests == null)
				// Plugins.walkSelectionServices(new ICallback<ISelectionService>() {
				// @Override
				// public void process(ISelectionService t) throws Exception {
				// t.addPostSelectionListener(selectionListener);
				// }
				// });
				// Plugins.addWindowListener(new IWindowListener() {
				//
				// @Override
				// public void windowOpened(IWorkbenchWindow window) {
				// System.out.println("Window opened");
				// }
				//
				// @Override
				// public void windowDeactivated(IWorkbenchWindow window) {
				// System.out.println("Window deactivated");
				// }
				//
				// @Override
				// public void windowClosed(IWorkbenchWindow window) {
				// System.out.println("Window closed");
				// }
				//
				// @Override
				// public void windowActivated(IWorkbenchWindow window) {
				// System.out.println("Window activated");
				// }
				// });
			}

		}
		return selectionBindingManager;
	}

	ExecutorService getExecutorService() {
		return executor == null ? executor = new ThreadPoolExecutor(CommonConstants.startThreadSize, CommonConstants.maxThreadSize, CommonConstants.threadStayAliveTimeMs, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(CommonConstants.maxOutStandingJobs)) : executor;
	}

	public SoftwareFmContainer<ITextSelection> getContainer() {
		return container == null ? container = makeContainer() : container;
	}

	private SoftwareFmContainer<ITextSelection> makeContainer() {
		synchronized (lock) {
			IResourceGetter resourceGetter = getResourceGetter();
			IMaven maven = IMaven.Utils.makeImport();
			IUrlStrategy urlStrategy = getUrlStrategy();
			IArtifactDataCache projectDataCache = getArtifactDataCache();
			IMakeLink makeLink = IMakeLink.Utils.makeLink(urlStrategy, projectDataCache);
			ISelectedBindingManager<ITextSelection> selectionBindingManager = getSelectionBindingManager();
			MavenImportJob mavenImport = new MavenImportJob(maven, makeLink, resourceGetter, selectionBindingManager);
			ManualImportJob manualImport = new ManualImportJob(makeLink, resourceGetter, selectionBindingManager);
			ITemplateStore templateStore = ITemplateStore.Utils.templateStore(urlStrategy);
			ImageRegistry imageRegistry = SoftwareFmPlugin.getDefault().getImageRegistry();
			return container == null ? new SoftwareFmContainer<ITextSelection>(resourceGetter, selectionBindingManager, mavenImport, manualImport, urlStrategy, templateStore, projectDataCache, getActionState(), imageRegistry) : container;
		}
	}

	IArtifactDataCache getArtifactDataCache() {
		if (artifactDataCache == null)
			synchronized (lock) {
				if (artifactDataCache == null)
					artifactDataCache = IArtifactDataCache.Utils.artifactDataCache();
			}
		return artifactDataCache;
	}

	private IUrlStrategy getUrlStrategy() {
		if (urlStrategy == null)
			synchronized (lock) {
				if (urlStrategy == null)
					urlStrategy = IUrlStrategy.Utils.withActionBarState(IUrlStrategy.Utils.urlStrategy(), getActionState());
			}
		return urlStrategy;
	}

	public SfmActionState getActionState() {
		if (sfmActionState == null)
			synchronized (lock) {
				if (sfmActionState == null)
					sfmActionState = new SfmActionState();
			}
		return sfmActionState;
	}

	private IResourceGetter getResourceGetter() {
		return resourceGetter = resourceGetter == null ? IResourceGetter.Utils.resourceGetter(Marker.class, "text") : resourceGetter;
	}

	public void dispose() {
		synchronized (lock) {
			if (executor != null)
				executor.shutdown();
			executor = null;
			if (displayForTests == null)
				Plugins.walkSelectionServices(new ICallback<ISelectionService>() {
					public void process(ISelectionService t) throws Exception {
						t.removePostSelectionListener(selectionListener);
					}
				});
			if (selectionBindingManager != null)
				selectionBindingManager.dispose();
			selectionBindingManager = null;
			resourceGetter = null;
			artifactDataCache = null;
			container = null;
			urlStrategy = null;
			sfmActionState = null;
		}
	}
}
