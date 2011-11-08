package org.softwareFm.explorer.eclipse;

import java.util.Arrays;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;
import org.softwareFm.eclipse.ISelectedBindingManager;
import org.softwareFm.eclipse.Plugins;
import org.softwareFm.eclipse.SelectedArtifactSelectionManager;
import org.softwareFm.eclipse.SoftwareFmActivator;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.constants.HttpClientConstants;
import org.softwareFm.jdtBinding.api.IBindingRipper;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.repositoryFacard.impl.RepositoryFacard;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.softwareFm.explorer.eclipse.ExplorerView"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private IRepositoryFacard repository;

	private String uuid;

	private ISelectedBindingManager selectedArtifactSelectionManager;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		if (repository != null)
			repository.shutdown();
		repository = null;
		super.stop(context);
	}

	public IRepositoryFacard getRepository() {
		if (repository == null) {
			// String host = HttpClientConstants.defaultHost;// "178.79.180.172";
			// int port = HttpClientConstants.defaultPort;// 8080;
			IHttpClient client = IHttpClient.Utils.builder().withCredentials(HttpClientConstants.userName, HttpClientConstants.password).//
					setDefaultHeaders(Arrays.<NameValuePair> asList(new BasicNameValuePair("SoftwareFm", getUuid())));
			repository = new RepositoryFacard(client, "sfm");
		}
		return repository;
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

	public String getUuid() {
		return uuid == null ? uuid = findOrMakeUuid() : uuid;
	}

	private String findOrMakeUuid() {
		try {
			@SuppressWarnings("deprecation")
			IEclipsePreferences prefs = new InstanceScope().getNode(SoftwareFmActivator.PLUGIN_ID);
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

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
