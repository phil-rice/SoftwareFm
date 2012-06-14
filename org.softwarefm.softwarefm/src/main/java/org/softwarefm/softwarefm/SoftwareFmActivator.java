package org.softwarefm.softwarefm;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.softwarefm.eclipse.Marker;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.link.IMakeLink;
import org.softwarefm.eclipse.maven.IMaven;
import org.softwarefm.eclipse.selection.IProjectStrategy;
import org.softwarefm.eclipse.selection.ISelectedBindingListenerAndAdderRemover;
import org.softwarefm.eclipse.selection.ISelectedBindingManager;
import org.softwarefm.eclipse.selection.ISelectedBindingStrategy;
import org.softwarefm.eclipse.selection.internal.SelectedArtifactSelectionManager;
import org.softwarefm.eclipse.selection.internal.SoftwareFmProjectHtmlRipper;
import org.softwarefm.eclipse.selection.internal.SoftwareFmProjectStrategy;
import org.softwarefm.eclipse.selection.internal.SwtThreadSelectedBindingAggregator;
import org.softwarefm.eclipse.templates.ITemplateStore;
import org.softwarefm.eclipse.url.IUrlStrategy;
import org.softwarefm.softwarefm.jobs.ManualImportJob;
import org.softwarefm.softwarefm.jobs.MavenImportJob;
import org.softwarefm.softwarefm.plugins.Plugins;
import org.softwarefm.softwarefm.selection.internal.EclipseSelectedBindingStrategy;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.constants.CommonConstants;
import org.softwarefm.utilities.http.IHttpClient;
import org.softwarefm.utilities.resources.IResourceGetter;

/**
 * The activator class controls the plug-in life cycle
 */
public class SoftwareFmActivator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = SoftwareFmActivator.class.getPackage().getName();

	private static SoftwareFmActivator plugin;

	private static Display displayForTests;
	private final Object lock = new Object();

	private ISelectedBindingManager<ITextSelection> selectionBindingManager;

	private ExecutorService executor;

	private ISelectionListener selectionListener;

	private IResourceGetter resourceGetter;

	private SoftwareFmContainer<ITextSelection> container;

	private IUrlStrategy urlStrategy;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		dispose();
	}

	public static SoftwareFmActivator makeActivatorForTests(Display display) {
		displayForTests = display;
		return plugin == null ? plugin = new SoftwareFmActivator() : plugin;
	}

	public static SoftwareFmActivator getDefault() {
		return plugin;
	}

	protected Display getDisplay() {
		if (displayForTests != null)
			return displayForTests;
		else
			return PlatformUI.getWorkbench().getDisplay();
	}

	public ISelectedBindingManager<ITextSelection> getSelectionBindingManager() {
		return selectionBindingManager == null ? makeSelectionBindingManager() : selectionBindingManager;

	}

	private ISelectedBindingManager<ITextSelection> makeSelectionBindingManager() {
		synchronized (lock) {
			if (selectionBindingManager == null) {
				IUrlStrategy urlStrategy = getUrlStrategy();
				ISelectedBindingListenerAndAdderRemover<ITextSelection> listenerManager = new SwtThreadSelectedBindingAggregator<ITextSelection>(getDisplay());
				IProjectStrategy<ITextSelection> projectStrategy = new SoftwareFmProjectStrategy<ITextSelection>(IHttpClient.Utils.builder(), new SoftwareFmProjectHtmlRipper(), urlStrategy);
				ISelectedBindingStrategy<ITextSelection, Expression> strategy = new EclipseSelectedBindingStrategy(projectStrategy);
				ExecutorService executor = getExecutorService();
				selectionBindingManager = new SelectedArtifactSelectionManager<ITextSelection, Expression>(listenerManager, strategy, executor, ICallback.Utils.sysErrCallback());
				selectionListener = new ISelectionListener() {
					@Override
					public void selectionChanged(IWorkbenchPart part, ISelection selection) {
						if (selection instanceof ITextSelection)
							selectionBindingManager.selectionOccured((ITextSelection) selection);
						else
							selectionBindingManager.selectionOccured(null);
					}
				};
				if (displayForTests == null)
					Plugins.walkSelectionServices(new ICallback<ISelectionService>() {
						@Override
						public void process(ISelectionService t) throws Exception {
							t.addPostSelectionListener(selectionListener);
						}
					});
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
			IMakeLink makeLink = IMakeLink.Utils.makeLink(urlStrategy);
			MavenImportJob mavenImport = new MavenImportJob(maven, makeLink, resourceGetter);
			ManualImportJob manualImport = new ManualImportJob(makeLink, resourceGetter);
			ITemplateStore templateStore = ITemplateStore.Utils.templateStore(urlStrategy);
			return container == null ? new SoftwareFmContainer<ITextSelection>(resourceGetter, getSelectionBindingManager(), mavenImport, manualImport, urlStrategy, templateStore) : container;
		}
	}

	private IUrlStrategy getUrlStrategy() {
		return urlStrategy == null ? urlStrategy = IUrlStrategy.Utils.urlStrategy(CommonConstants.softwareFmHost) : urlStrategy;
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
					@Override
					public void process(ISelectionService t) throws Exception {
						t.removePostSelectionListener(selectionListener);
					}
				});
			if (selectionBindingManager != null)
				selectionBindingManager.dispose();
			selectionBindingManager = null;
			resourceGetter = null;
			container = null;
		}
	}
}
