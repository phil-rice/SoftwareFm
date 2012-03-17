/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.plugin;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;
import org.softwareFm.crowdsource.api.git.IGitLocal;
import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.api.git.IGitWriter;
import org.softwareFm.crowdsource.api.git.IRepoFinder;
import org.softwareFm.crowdsource.httpClient.IHttpClient;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.jdtBinding.IBindingRipper;
import org.softwareFm.eclipse.user.IProjectTimeGetter;
import org.softwareFm.swt.ICollectionConfigurationFactory;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.dataStore.ICardDataStore;
import org.softwareFm.swt.explorer.IUserDataListener;
import org.softwareFm.swt.explorer.IUserDataManager;
import org.softwareFm.swt.explorer.internal.UserData;

/**
 * The activator class controls the plug-in life cycle
 */
@SuppressWarnings("deprecation")
public class Activator extends AbstractUIPlugin {
	boolean local = false;

	// The plug-in ID
	public static final String PLUGIN_ID = "org.softwareFm.explorer.eclipse.ExplorerView"; //$NON-NLS-1$

	public static final boolean profile = true;

	// The shared instance
	private static Activator plugin;

	private String uuid;
	private ISelectedBindingManager selectedArtifactSelectionManager;
	private IServiceExecutor serviceExecutor;

	private IHttpClient httpClient;

	private IGitLocal gitLocal;

	private IUserDataManager userDataManager;

	public Activator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;

		if (serviceExecutor != null)
			serviceExecutor.shutdownAndAwaitTermination(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
		serviceExecutor = null;
		if (httpClient != null)
			httpClient.shutdown();
		httpClient = null;
		super.stop(context);
	}

	public IHttpClient getClient() {
		if (httpClient == null) {
			String server = local ? "localhost" : SoftwareFmConstants.softwareFmServerUrl;
			int port = local ? 8080 : 80;
			httpClient = IHttpClient.Utils.builder(server, port);
		}
		return httpClient;
	}

	public CardConfig getCardConfig(Composite parent) {
		final CardConfig cardConfig = ICollectionConfigurationFactory.Utils.softwareFmConfigurator().configure(//
				parent.getDisplay(), //
				new CardConfig(ICardFactory.Utils.cardFactory(), //
						ICardDataStore.Utils.repositoryCardDataStore(parent, getServiceExecutor(), getGitLocal())));
		return cardConfig;
	}

	public IRepoFinder getFindRepositoryRoot() {
		return new HttpRepoFinder(getClient(), CommonConstants.clientTimeOut);
	}

	public IGitWriter getGitWriter() {
		return new HttpGitWriter(getClient());
	}

	public IGitLocal getGitLocal() {
		if (gitLocal == null) {
			File home = new File(System.getProperty("user.home"));
			final File localRoot = new File(home, ".sfm");
			String remoteUriPrefix = local ? new File(home, ".sfm_remote").getAbsolutePath() : SoftwareFmConstants.gitProtocolAndGitServerName;
			IGitOperations gitOperations = IGitOperations.Utils.gitOperations(localRoot);
			gitLocal = IGitLocal.Utils.localReader(getFindRepositoryRoot(), gitOperations, getGitWriter(), remoteUriPrefix, CommonConstants.staleCachePeriod);
		}
		return gitLocal;
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

	public IProjectTimeGetter getProjectTimeGetter() {
		return IProjectTimeGetter.Utils.timeGetter();
	}

	public IUserDataManager getUserDataManager() {
		return userDataManager == null ? userDataManager = makeUserDataManager() : userDataManager;
	}

	protected IUserDataManager makeUserDataManager() {
		try {
			IUserDataManager result = IUserDataManager.Utils.userDataManager();
			String softwareFmId = getOr(LoginConstants.softwareFmIdKey, null);
			String email = getOr(LoginConstants.emailKey, null);
			String crypto = getOr(LoginConstants.cryptoKey, null);
			result.setUserData(this, new UserData(email, softwareFmId, crypto));
			result.addUserDataListener(new IUserDataListener() {
				@Override
				public void userDataChanged(Object source, UserData userData) {
					try {
						preferencesPut(LoginConstants.softwareFmIdKey, userData.softwareFmId);
						preferencesPut(LoginConstants.emailKey, userData.email);
						preferencesPut(LoginConstants.cryptoKey, userData.crypto);
					} catch (Exception e) {
						throw WrappedException.wrap(e);
					}
				}
			});
			return result;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	protected void preferencesPut(String key, String value) {
		Preferences userDataPreferences = getPluginPreferences();
		if (value == null)
			userDataPreferences.setToDefault(key);
		else
			userDataPreferences.setValue(key, value);
	}

	private String getOr(String key, String object) {
		// This is using a deprecated approach, as this is supported on older versions of eclipse
		Preferences userDataPreferences = getPluginPreferences();
		if (userDataPreferences.contains(key))
			return userDataPreferences.getString(key);
		else
			return object;
	}

}