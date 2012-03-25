/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.plugin;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.softwareFm.crowdsource.api.ICrowdSourcedApi;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.utilities.collections.Files;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.resources.IResourceGetter;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;
import org.softwareFm.eclipse.actions.IActionBar;
import org.softwareFm.eclipse.jdtBinding.BindingRipperResult;
import org.softwareFm.eclipse.mysoftwareFm.MyDetails;
import org.softwareFm.eclipse.mysoftwareFm.MyGroups;
import org.softwareFm.eclipse.mysoftwareFm.MyPeople;
import org.softwareFm.eclipse.snippets.SnippetFeedConfigurator;
import org.softwareFm.jarAndClassPath.api.ISoftwareFmApiFactory;
import org.softwareFm.jarAndClassPath.api.IUserDataManager;
import org.softwareFm.swt.browser.IBrowserConfigurator;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.constants.CollectionConstants;
import org.softwareFm.swt.constants.DisplayConstants;
import org.softwareFm.swt.explorer.IExplorer;
import org.softwareFm.swt.explorer.IMasterDetailSocial;
import org.softwareFm.swt.explorer.IShowMyData;
import org.softwareFm.swt.explorer.IShowMyGroups;
import org.softwareFm.swt.explorer.IShowMyPeople;
import org.softwareFm.swt.login.ILoginStrategy;
import org.softwareFm.swt.menu.ICardMenuItemHandler;
import org.softwareFm.swt.swt.Swts.Size;
import org.softwareFm.swt.timeline.IPlayListGetter;

public class ExplorerView extends ViewPart {

	protected IActionBar actionBar;

	@Override
	public void createPartControl(Composite parent) {
		final Activator activator = Activator.getDefault();
		final IMasterDetailSocial masterDetailSocial = IMasterDetailSocial.Utils.masterDetailSocial(parent);
		Size.resizeMeToParentsSize(masterDetailSocial.getControl());
		final CardConfig cardConfig = activator.getCardConfig(parent);

		ICrowdSourcedApi api = activator.local ? ISoftwareFmApiFactory.Utils.makeClientApiForLocalHost() : ISoftwareFmApiFactory.Utils.makeClientApiForSoftwareFmServer();
		final IContainer readWriteApi = api.makeContainer();

		IPlayListGetter playListGetter = new ArtifactPlayListGetter(cardConfig.cardDataStore);
		IServiceExecutor service = activator.getServiceExecutor();
		IShowMyData showMyDetails = MyDetails.showMyDetails(readWriteApi, service, cardConfig, masterDetailSocial);
		IShowMyGroups showMyGroups = MyGroups.showMyGroups(masterDetailSocial, readWriteApi, service, false, cardConfig);
		IShowMyPeople showMyPeople = MyPeople.showMyPeople(readWriteApi, service, masterDetailSocial, cardConfig, CommonConstants.clientTimeOut * 2);
		Callable<Long> timeGetter = Callables.time();
		List<String> rootUrls = getRootUrls();
		ILoginStrategy loginStrategy = ILoginStrategy.Utils.softwareFmLoginStrategy(parent.getDisplay(), activator.getServiceExecutor(), readWriteApi);

		IUserDataManager userDataManager = activator.getUserDataManager();
		final IExplorer explorer = IExplorer.Utils.explorer(masterDetailSocial, readWriteApi, cardConfig, rootUrls, playListGetter, service, loginStrategy, showMyDetails, showMyGroups, showMyPeople, userDataManager, timeGetter);
		actionBar = makeActionBar(explorer, cardConfig);
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		actionBar.populateToolbar(toolBarManager);
		addMenuItems(explorer);

		IBrowserConfigurator.Utils.configueWithUrlRssTweet(explorer);
		SnippetFeedConfigurator.configure(explorer, cardConfig);

		final IResourceGetter resourceGetter = Functions.call(cardConfig.resourceGetterFn, null);
		String welcomeUrl = IResourceGetter.Utils.getOrException(resourceGetter, CardConstants.webPageWelcomeUrl);

		ISelectedBindingManager selectedBindingManager = activator.getSelectedBindingManager();// creates it

		selectedBindingManager.addSelectedArtifactSelectionListener(new ISelectedBindingListener() {
			@Override
			public void selectionOccured(final BindingRipperResult ripperResult) {
				ExplorerView.this.actionBar.selectionOccured(ripperResult);
			}
		});
		explorer.showMySoftwareFm();
		explorer.processUrl(DisplayConstants.browserFeedType, welcomeUrl);
	}

	protected IActionBar makeActionBar(final IExplorer explorer, final CardConfig cardConfig) {
		return IActionBar.Utils.actionBar(explorer, cardConfig, SelectedArtifactSelectionManager.reRipFn(), false);
	}

	protected void addMenuItems(final IExplorer explorer) {
		ICardMenuItemHandler.Utils.addSoftwareFmMenuItemHandlers(explorer);
	}

	protected List<String> getRootUrls() {
		return CollectionConstants.rootUrlList;
	}

	protected void processNoData(CardConfig cardConfig, final IExplorer explorer, final IResourceGetter resourceGetter, final BindingRipperResult ripperResult) {
		final String hexDigest = ripperResult.hexDigest;
		final String unknownJarUrl = IResourceGetter.Utils.getOrException(resourceGetter, CardConstants.webPageUnknownJarUrl);
		File file = ripperResult.path.toFile();
		if (Files.extension(file.toString()).equals("jar")) {
			explorer.displayUnrecognisedJar(file, hexDigest, ripperResult.javaProject.getElementName());
			explorer.processUrl(DisplayConstants.browserFeedType, unknownJarUrl);
		}
	}

	@Override
	public void setFocus() {
	}

}