/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.explorer.eclipse;

import java.util.Arrays;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.ICardDataStore;
import org.softwareFm.collections.ICollectionConfigurationFactory;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.constants.HttpClientConstants;
import org.softwareFm.jdtBinding.api.IBindingRipper;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.repositoryFacard.impl.RepositoryFacard;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.services.IServiceExecutor;

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

	private IServiceExecutor serviceExecutor;

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

		if (serviceExecutor != null)
			serviceExecutor.shutdown();
		serviceExecutor = null;
		super.stop(context);
	}

	public CardConfig getCardConfig(Composite parent) {
		final CardConfig cardConfig = ICollectionConfigurationFactory.Utils.softwareFmConfigurator().configure(parent.getDisplay(), new CardConfig(ICardFactory.Utils.cardFactory(), ICardDataStore.Utils.repositoryCardDataStore(parent, getRepository())));
		return cardConfig;
	}

	public IRepositoryFacard getRepository() {
		if (repository == null) {
			// String host = HttpClientConstants.defaultHost;// "178.79.180.172";
			// int port = HttpClientConstants.defaultPort;// 8080;
			IHttpClient client = IHttpClient.Utils.builder().withCredentials(HttpClientConstants.userName, HttpClientConstants.password).//
					setDefaultHeaders(Arrays.<NameValuePair> asList(new BasicNameValuePair("SoftwareFm", getUuid())));
			repository = new RepositoryFacard(client, "1.json");
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

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public IServiceExecutor getServiceExecutor() {
		return serviceExecutor == null ? serviceExecutor = IServiceExecutor.Utils.defaultExecutor() : serviceExecutor;
	}

}