package org.softwarefm.eclipse;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.softwarefm.core.Marker;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.actions.SfmActionState;
import org.softwarefm.core.cache.IArtifactDataCache;
import org.softwarefm.core.composite.SoftwareFmComposite;
import org.softwarefm.core.constants.ImageConstants;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.core.link.IMakeLink;
import org.softwarefm.core.maven.IMaven;
import org.softwarefm.core.selection.IArtifactStrategy;
import org.softwarefm.core.selection.ISelectedBindingListenerAndAdderRemover;
import org.softwarefm.core.selection.ISelectedBindingManager;
import org.softwarefm.core.selection.ISelectedBindingStrategy;
import org.softwarefm.core.selection.SelectedBindingAdapter;
import org.softwarefm.core.selection.internal.SelectedArtifactSelectionManager;
import org.softwarefm.core.selection.internal.SoftwareFmArtifactHtmlRipper;
import org.softwarefm.core.selection.internal.SoftwareFmArtifactStrategy;
import org.softwarefm.core.selection.internal.SwtThreadSelectedBindingAggregator;
import org.softwarefm.core.templates.ITemplateStore;
import org.softwarefm.core.url.HostOffsetAndUrl;
import org.softwarefm.core.url.IUrlStrategy;
import org.softwarefm.eclipse.jobs.Jobs;
import org.softwarefm.eclipse.jobs.ManualImportJob;
import org.softwarefm.eclipse.jobs.MavenImportJob;
import org.softwarefm.eclipse.plugins.Plugins;
import org.softwarefm.eclipse.plugins.WorkbenchWindowListenerManager;
import org.softwarefm.eclipse.selection.internal.EclipseSelectedBindingStrategy;
import org.softwarefm.shared.constants.CommonConstants;
import org.softwarefm.shared.social.FriendData;
import org.softwarefm.shared.social.IFoundFriendsListener;
import org.softwarefm.shared.social.IFoundNameListener;
import org.softwarefm.shared.social.ISocialManager;
import org.softwarefm.shared.social.internal.SocialManager;
import org.softwarefm.shared.usage.IUsage;
import org.softwarefm.shared.usage.IUsageFromServer;
import org.softwarefm.shared.usage.IUsageFromServerCallback;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.shared.usage.IUsageReporter;
import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.shared.usage.UsageConstants;
import org.softwarefm.shared.usage.internal.Usage;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.callbacks.ICallback2;
import org.softwarefm.utilities.collections.Files;
import org.softwarefm.utilities.events.IMultipleListenerList;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.http.IHttpClient;
import org.softwarefm.utilities.maps.Maps;
import org.softwarefm.utilities.resources.IResourceGetter;

/**
 * The activator class controls the plug-in life cycle
 */
public class SoftwareFmPlugin extends AbstractUIPlugin implements IStartup {

	public static final String PLUGIN_ID = SoftwareFmPlugin.class.getPackage().getName();

	private static SoftwareFmPlugin plugin;

	private static Display displayForTests;
	private final Object lock = new Object();
	private final Object socialFilelock = new Object();

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

	private IMultipleListenerList multipleListenerList;

	private ISocialManager socialManager;

	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println(getClass().getSimpleName() + ".start");
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
			if (usage != null)
				return usage;
			System.out.println("Starting makeUsage");
			final Usage usage = new Usage();
			final Job job = new Job("Long Running Job") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						sendUsage();
						return Status.OK_STATUS;
					} finally {
						schedule(UsageConstants.updatePeriod);
					}
				}
			};
			job.setSystem(true);
			job.schedule(UsageConstants.updatePeriod);
			return usage;
		}
	}

	public void sendUsage() {
		try {
			final IUsageReporter reporter = IUsageReporter.Utils.reporter();
			String myName = getSocialManager().myName();
			if (myName != null && recordUsage()) {
				reporter.report(myName, usage.getStats());
				usage.nuke();
			}
			loadUsageFor(getSocialManager().myName());
			loadUsageFor(getSocialManager().myFriends());
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public IMultipleListenerList getMultipleListenerList() {
		return multipleListenerList == null ? multipleListenerList = IMultipleListenerList.Utils.defaultList() : multipleListenerList;
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
				IMultipleListenerList listenerList = getMultipleListenerList();
				ISelectedBindingListenerAndAdderRemover<ITextSelection> listenerManager = new SwtThreadSelectedBindingAggregator<ITextSelection>(getDisplay(), listenerList);
				IArtifactStrategy<ITextSelection> projectStrategy = new SoftwareFmArtifactStrategy<ITextSelection>(IHttpClient.Utils.builder(), new SoftwareFmArtifactHtmlRipper(), urlStrategy);
				ISelectedBindingStrategy<ITextSelection, Expression> strategy = new EclipseSelectedBindingStrategy(projectStrategy);
				ExecutorService executor = getExecutorService();
				SelectedArtifactSelectionManager<ITextSelection, Expression> selectionBindingManager = new SelectedArtifactSelectionManager<ITextSelection, Expression>(listenerManager, strategy, executor, getArtifactDataCache(), ICallback.Utils.sysErrCallback());
				new WorkbenchWindowListenerManager(selectionBindingManager);
				selectionBindingManager.addSelectedArtifactSelectionListener(new SelectedBindingAdapter() {
					@Override
					public void codeSelectionOccured(CodeData codeData, int selectionCount) {
						if (codeData != null && codeData.className != null) {
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
					public boolean isValid() {
						return true;
					}
				});
				return selectionBindingManager;
			}

		}
		return selectionBindingManager;
	}

	ExecutorService getExecutorService() {
		return executor == null ? executor = new ThreadPoolExecutor(CommonConstants.startThreadSize, CommonConstants.maxThreadSize, CommonConstants.threadStayAliveTimeMs, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(CommonConstants.maxOutStandingJobs)) : executor;
	}

	public ISocialManager getSocialManager() {
		return socialManager == null ? makeSocialManager() : socialManager;
	}

	private ISocialManager makeSocialManager() {
		synchronized (lock) {
			if (socialManager != null)
				return socialManager;
			return socialManager = new SocialManager(getMultipleListenerList(), IUsagePersistance.Utils.persistance());
		}
	}

	public SoftwareFmContainer<ITextSelection> getContainer() {
		return container == null ? container = makeContainer() : container;
	}

	private SoftwareFmContainer<ITextSelection> makeContainer() {
		synchronized (lock) {
			if (container != null)
				return container;
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
			IUsagePersistance persistance = IUsagePersistance.Utils.persistance();
			IMultipleListenerList listenerList = IMultipleListenerList.Utils.defaultList();
			final IUsageFromServer usageFromServer = IUsageFromServer.Utils.usageFromServer(UsageConstants.host, UsageConstants.port, persistance);
			final ISocialManager socialManager = getSocialManager();

			File socialFile = getSocialFile();
			if (socialFile.exists()) {
				try {
					synchronized (socialFilelock) {
						System.out.println("Loading social manager from file");
						socialManager.populate(Files.getText(socialFile));
					}
				} catch (Exception e) {
					e.printStackTrace();
					socialManager.clearUsageData();
				}
			}

			socialManager.addFoundNameListener(new IFoundNameListener() {
				@Override
				public void foundName(final String name) {
					loadUsageFor(name);
				}

			});
			socialManager.addFoundFriendsListener(new IFoundFriendsListener() {
				@Override
				public void foundFriends(final List<FriendData> friends) {
					loadUsageFor(friends);

				}
			});
			SoftwareFmContainer<ITextSelection> softwareFmContainer = new SoftwareFmContainer<ITextSelection>(resourceGetter, selectionBindingManager, mavenImport, manualImport, urlStrategy, templateStore, projectDataCache, getActionState(), imageRegistry, listenerList, persistance, usageFromServer, socialManager);
			return softwareFmContainer;
		}
	}

	private void loadUsageFor(final String name) {
		Jobs.run("Find Usage for " + name, new Runnable() {
			public void run() {
				System.out.println("In job find Usage for " + name);
				getContainer().usageFromServer.getStatsFor(name, new IUsageFromServerCallback() {
					@Override
					public void foundStats(String name, IUsageStats usageStats) {
						socialManager.setUsageData(name, usageStats);
					}
				});
				saveSocialManager();
			}
		});
	}

	private void loadUsageFor(final List<FriendData> friends) {
		Jobs.run("Find Usage for Friends of " + socialManager.myName(), new Runnable() {
			@Override
			public void run() {
				System.out.println("In job find Usage for friends");
				getContainer().usageFromServer.getStatsFor(friends, new IUsageFromServerCallback() {
					@Override
					public void foundStats(String name, IUsageStats usageStats) {
						socialManager.setUsageData(name, usageStats);
					}
				});
				saveSocialManager();
			}

		});
	}

	private void saveSocialManager() {
		synchronized (socialFilelock) {
			System.out.println("Saving social manager");
			File socialFile = getSocialFile();
			socialFile.getParentFile().mkdirs();
			Files.setText(socialFile, getSocialManager().serialize());
		}
	}

	public File getSocialFile() {
		File socialFile = new File(SoftwareFmPlugin.getDefault().getStateLocation().toFile().getParent(), getClass().getPackage().getName() + File.separator + UsageConstants.socialManagerFileName);
		return socialFile;
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
			multipleListenerList = null;
			socialManager = null;
			usage = null;
		}
	}

	@Override
	public void earlyStartup() {
		System.out.println(getClass().getSimpleName() + ".earlyStartup");
	}

}
