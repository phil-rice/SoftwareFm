/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.plugin;

import java.io.File;
import java.util.List;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.collections.Files;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.services.IServiceExecutor;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.IRequestGroupReportGeneration;
import org.softwareFm.eclipse.actions.IActionBar;
import org.softwareFm.eclipse.jdtBinding.BindingRipperResult;
import org.softwareFm.eclipse.mysoftwareFm.MyDetails;
import org.softwareFm.eclipse.mysoftwareFm.MyGroups;
import org.softwareFm.eclipse.mysoftwareFm.RequestGroupReportGeneration;
import org.softwareFm.eclipse.snippets.SnippetFeedConfigurator;
import org.softwareFm.eclipse.usage.IUsageStrategy;
import org.softwareFm.eclipse.user.IProjectTimeGetter;
import org.softwareFm.swt.browser.IBrowserConfigurator;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.constants.CollectionConstants;
import org.softwareFm.swt.constants.DisplayConstants;
import org.softwareFm.swt.explorer.IExplorer;
import org.softwareFm.swt.explorer.IMasterDetailSocial;
import org.softwareFm.swt.explorer.IShowMyData;
import org.softwareFm.swt.explorer.IShowMyGroups;
import org.softwareFm.swt.menu.ICardMenuItemHandler;
import org.softwareFm.swt.mySoftwareFm.ILoginStrategy;
import org.softwareFm.swt.swt.Swts.Size;
import org.softwareFm.swt.timeline.IPlayListGetter;

public class ExplorerView extends ViewPart {

	protected IActionBar actionBar;

	@Override
	public void createPartControl(Composite parent) {
		final Activator activator = Activator.getDefault();
		final CardConfig cardConfig = makeCardConfig(parent);
		IMasterDetailSocial masterDetailSocial = IMasterDetailSocial.Utils.masterDetailSocial(parent);
		Size.resizeMeToParentsSize(masterDetailSocial.getControl());

		IPlayListGetter playListGetter = new ArtifactPlayListGetter(cardConfig.cardDataStore);
		IServiceExecutor service = activator.getServiceExecutor();
		ILoginStrategy loginStrategy = ILoginStrategy.Utils.softwareFmLoginStrategy(parent.getDisplay(), activator.getServiceExecutor(), activator.getClient());
		IUrlGenerator userUrlGenerator = cardConfig.urlGeneratorMap.get(CardConstants.userUrlKey);
		IUrlGenerator groupUrlGenerator = GroupConstants.groupsGenerator();
		IGitLocal gitLocal= activator.getGitLocal();
		IProjectTimeGetter timeGetter = activator.getProjectTimeGetter();
		IRequestGroupReportGeneration reportGenerator = new RequestGroupReportGeneration(activator.getClient(), IResponseCallback.Utils.sysoutStatusCallback());

		IShowMyData showMyDetails = MyDetails.showMyDetails(service, cardConfig, masterDetailSocial, userUrlGenerator, gitLocal, timeGetter);
		IShowMyGroups showMyGroups = MyGroups.showMyGroups(service, cardConfig, masterDetailSocial, userUrlGenerator, groupUrlGenerator, gitLocal, timeGetter, reportGenerator);
		
		final IExplorer explorer = IExplorer.Utils.explorer(masterDetailSocial, cardConfig, getRootUrls(), playListGetter, service, loginStrategy, showMyDetails, showMyGroups);
		IUsageStrategy usageStrategy = IUsageStrategy.Utils.usage(activator.getServiceExecutor(), activator.getClient(), gitLocal, userUrlGenerator);
		actionBar = makeActionBar(explorer, cardConfig, usageStrategy);
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

	protected IActionBar makeActionBar(final IExplorer explorer, final CardConfig cardConfig, IUsageStrategy usageStrategy) {
		return IActionBar.Utils.actionBar(explorer, cardConfig, SelectedArtifactSelectionManager.reRipFn(), false, usageStrategy);
	}

	protected void addMenuItems(final IExplorer explorer) {
		ICardMenuItemHandler.Utils.addSoftwareFmMenuItemHandlers(explorer);
	}

	protected List<String> getRootUrls() {
		return CollectionConstants.rootUrlList;
	}

	protected CardConfig makeCardConfig(Composite parent) {
		Activator activator = Activator.getDefault();
		return activator.getCardConfig(parent);
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