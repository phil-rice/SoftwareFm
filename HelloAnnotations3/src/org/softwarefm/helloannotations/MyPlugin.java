package org.softwarefm.helloannotations;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.osgi.framework.BundleContext;
import org.softwarefm.helloannotations.annotations.IMarkerStore;
import org.softwarefm.helloannotations.annotations.MarkUpResource;
import org.softwarefm.helloannotations.annotations.internal.WikiMarkerStore;
import org.softwarefm.helloannotations.editor.MyPartitionScanner;
import org.softwarefm.shared.constants.CommonConstants;
import org.softwarefm.utilities.callbacks.ICallback2;

/**
 * The activator class controls the plug-in life cycle
 */
public class MyPlugin extends AbstractUIPlugin implements IStartup {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.softwarefm.helloAnnotations"; //$NON-NLS-1$

	public final static String MY_PARTITIONING = "__org.softwarefm.mypartitioning__";

	private MyPartitionScanner fPartitionScanner;

	public MyPartitionScanner getMyPartitionScanner() {
		if (fPartitionScanner == null)
			fPartitionScanner = new MyPartitionScanner();
		return fPartitionScanner;
	}

	// The shared instance
	private static MyPlugin plugin;

	/**
	 * The constructor
	 */
	public MyPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		final IWorkbench workbench = PlatformUI.getWorkbench();
		AllWorkbenchDoer.forEveryEditorNowAndWhenOpens(workbench, new ICallback2<IFile, AbstractDecoratedTextEditor>() {
			@Override
			public void process(final IFile file, final AbstractDecoratedTextEditor editor) throws Exception {
				final IMarkerStore store = IMarkerStore.Utils.mock(//
						"org.softwarefm.httpServer/IRegistryConfigurator", "This is the new value for IRegistryConfig");
				WikiMarkerStore wikiStore = new WikiMarkerStore(CommonConstants.softwareFmHost, CommonConstants.softwareFmApiOffset);
				MarkUpResource markUpResource = new MarkUpResource(wikiStore, "org.softwarefm.code.marker");
				markUpResource.markup(file, editor);
			}
		});
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static MyPlugin getDefault() {
		return plugin;
	}

	@Override
	public void earlyStartup() {

	}

}

