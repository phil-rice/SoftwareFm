/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.explorer.eclipse;

import java.io.File;
import java.util.List;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.collections.actions.IActionBar;
import org.softwareFm.collections.constants.CollectionConstants;
import org.softwareFm.collections.explorer.IExplorer;
import org.softwareFm.collections.explorer.IMasterDetailSocial;
import org.softwareFm.collections.explorer.IUsageStrategy;
import org.softwareFm.collections.explorer.SnippetFeedConfigurator;
import org.softwareFm.collections.menu.ICardMenuItemHandler;
import org.softwareFm.collections.mySoftwareFm.ILoginStrategy;
import org.softwareFm.display.browser.IBrowserConfigurator;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.swt.Swts.Size;
import org.softwareFm.display.timeline.IPlayListGetter;
import org.softwareFm.jdtBinding.api.BindingRipperResult;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.services.IServiceExecutor;

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
		IUsageStrategy usageStrategy  = IUsageStrategy.Utils.usage(activator.getServiceExecutor(), activator.getClient(), activator.getGitServer(), ServerConstants.userGenerator(), ServerConstants.projectGenerator());
		final IExplorer explorer = IExplorer.Utils.explorer(masterDetailSocial, cardConfig, getRootUrls(), playListGetter, service, loginStrategy, usageStrategy);
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

	protected CardConfig makeCardConfig(Composite parent) {
		return Activator.getDefault().getCardConfig(parent);
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