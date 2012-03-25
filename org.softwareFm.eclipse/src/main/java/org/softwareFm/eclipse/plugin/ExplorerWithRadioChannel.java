/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.plugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.crowdsource.api.ICrowdSourcedApi;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.httpClient.IHttpClient;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;
import org.softwareFm.eclipse.mysoftwareFm.MyDetails;
import org.softwareFm.eclipse.mysoftwareFm.MyGroups;
import org.softwareFm.eclipse.mysoftwareFm.MyPeople;
import org.softwareFm.eclipse.snippets.SnippetFeedConfigurator;
import org.softwareFm.jarAndClassPath.api.ISoftwareFmApiFactory;
import org.softwareFm.jarAndClassPath.api.IUserDataManager;
import org.softwareFm.swt.ICollectionConfigurationFactory;
import org.softwareFm.swt.browser.IBrowserConfigurator;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardChangedListener;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.card.ICardHolder;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.dataStore.CardAndCollectionDataStoreAdapter;
import org.softwareFm.swt.dataStore.IAfterEditCallback;
import org.softwareFm.swt.dataStore.ICardDataStore;
import org.softwareFm.swt.dataStore.IMutableCardDataStore;
import org.softwareFm.swt.explorer.IExplorer;
import org.softwareFm.swt.explorer.IExplorerListener;
import org.softwareFm.swt.explorer.IMasterDetailSocial;
import org.softwareFm.swt.explorer.IShowMyData;
import org.softwareFm.swt.explorer.IShowMyGroups;
import org.softwareFm.swt.explorer.IShowMyPeople;
import org.softwareFm.swt.login.ILoginStrategy;
import org.softwareFm.swt.menu.ICardMenuItemHandler;
import org.softwareFm.swt.swt.Swts;
import org.softwareFm.swt.swt.Swts.Buttons;
import org.softwareFm.swt.swt.Swts.Grid;
import org.softwareFm.swt.timeline.IPlayListGetter;

public class ExplorerWithRadioChannel {
	private static boolean local = false;

	public static void main(String[] args) {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ERROR);
		// Logger.getLogger(IGitLocal.class).setLevel(Level.DEBUG);
		Logger.getLogger(IHttpClient.class).setLevel(Level.DEBUG);

		final IServiceExecutor serviceExecutor = IServiceExecutor.Utils.defaultExecutor();
		final AtomicReference<ICrowdSourcedApi> forShutdown = new AtomicReference<ICrowdSourcedApi>();

		try {
			final List<String> rootUrls = Arrays.asList("/softwareFm/data", "/softwareFm/snippet");
			final String firstUrl = "/softwareFm/data/ant/ant/artifact/ant";
			Swts.Show.display(ExplorerWithRadioChannel.class.getSimpleName(), new IFunction1<Composite, Composite>() {
				@Override
				public Composite apply(Composite from) throws Exception {
					Composite explorerAndButton = Swts.newComposite(from, SWT.NULL, "ExplorerAndButton");
					Composite buttonPanel = Swts.newComposite(explorerAndButton, SWT.NULL, "ButtonPanel");
					final IMasterDetailSocial masterDetailSocial = IMasterDetailSocial.Utils.masterDetailSocial(explorerAndButton);
					explorerAndButton.setLayout(new GridLayout());
					buttonPanel.setLayout(Swts.Row.getHorizonalNoMarginRowLayout());

					ICrowdSourcedApi api = local ? ISoftwareFmApiFactory.Utils.makeClientApiForLocalHost() : ISoftwareFmApiFactory.Utils.makeClientApiForSoftwareFmServer();
					final IContainer readWriteApi = api.makeContainer();
					forShutdown.set(api);

					final IMutableCardDataStore cardDataStore = ICardDataStore.Utils.repositoryCardDataStore(from, serviceExecutor, readWriteApi);
					ICardFactory cardFactory = ICardFactory.Utils.cardFactory();
					final CardConfig cardConfig = ICollectionConfigurationFactory.Utils.softwareFmConfigurator().configure(from.getDisplay(), new CardConfig(cardFactory, cardDataStore));

					IPlayListGetter playListGetter = new ArtifactPlayListGetter(cardDataStore);
					ILoginStrategy loginStrategy = ILoginStrategy.Utils.softwareFmLoginStrategy(from.getDisplay(), serviceExecutor, readWriteApi);
					final IUserDataManager userDataManager = IUserDataManager.Utils.userDataManager();
					IShowMyData showMyDetails = MyDetails.showMyDetails(readWriteApi, serviceExecutor, cardConfig, masterDetailSocial);
					IShowMyGroups showMyGroups = MyGroups.showMyGroups(masterDetailSocial, readWriteApi, serviceExecutor, false, cardConfig);
					IShowMyPeople showMyPeople = MyPeople.showMyPeople(readWriteApi, serviceExecutor, masterDetailSocial, cardConfig, CommonConstants.clientTimeOut * 2);
					final IExplorer explorer = IExplorer.Utils.explorer(masterDetailSocial, readWriteApi, cardConfig, rootUrls, playListGetter, serviceExecutor, loginStrategy, showMyDetails, showMyGroups, showMyPeople, userDataManager, Callables.time());

					ICardMenuItemHandler.Utils.addSoftwareFmMenuItemHandlers(explorer);
					IBrowserConfigurator.Utils.configueWithUrlRssTweet(explorer);
					SnippetFeedConfigurator.configure(explorer, cardConfig);

					explorer.displayCard(firstUrl, new CardAndCollectionDataStoreAdapter());
					buttonPanel.setLayoutData(Grid.makeGrabHorizonalAndFillGridData());
					masterDetailSocial.getComposite().setLayoutData(Grid.makeGrabHorizonalVerticalAndFillGridData());

					Buttons.makePushButton(buttonPanel, null, "Next", false, new Runnable() {
						@Override
						public void run() {
							explorer.next();
						}
					});
					Buttons.makePushButton(buttonPanel, null, "Prev", false, new Runnable() {
						@Override
						public void run() {
							explorer.previous();
						}
					});
					final AtomicReference<String> url = new AtomicReference<String>();
					explorer.addCardListener(new ICardChangedListener() {

						@Override
						public void valueChanged(ICard card, String key, Object newValue) {

						}

						@Override
						public void cardChanged(ICardHolder cardHolder, ICard card) {
							url.set(card.url());
						}
					});
					Buttons.makePushButton(buttonPanel, null, "Select Artifact", false, new Runnable() {
						@Override
						public void run() {
							explorer.selectAndNext(url.get());
						}
					});
					Buttons.makePushButton(buttonPanel, null, "Refresh", false, new Runnable() {
						@Override
						public void run() {
							explorer.clearCaches();
							String url = explorer.getCardHolder().getCard().url();
							explorer.displayCard(url, new CardAndCollectionDataStoreAdapter());
						}
					});
					Buttons.makePushButton(buttonPanel, null, "Unrecognised Jar", false, new Runnable() {
						@Override
						public void run() {
							explorer.displayUnrecognisedJar(new File("a/b/cake-forkjoin-0.1.jar"), "0123439754987345978", "some project name");
						}
					});
					Buttons.makePushButton(buttonPanel, null, "Not A Jar", false, new Runnable() {
						@Override
						public void run() {
							explorer.displayNotAJar();
						}
					});
					Buttons.makePushButton(buttonPanel, null, "Artifact", false, new Runnable() {
						@Override
						public void run() {
							explorer.displayCard(firstUrl, new CardAndCollectionDataStoreAdapter());
						}
					});

					Buttons.makePushButton(buttonPanel, "people I know using Junit", new Runnable() {
						@Override
						public void run() {
							explorer.showPeople("junit", "junit");
						}
					});
					Buttons.makePushButton(buttonPanel, null, "Nuke", false, new Runnable() {
						@Override
						public void run() {
							cardConfig.cardDataStore.delete(explorer.getCardHolder().getCard().url(), new IAfterEditCallback() {
								@Override
								public void afterEdit(String url) {
									explorer.displayCard(url, new CardAndCollectionDataStoreAdapter());
								}
							});
						}
					});
					Buttons.makePushButton(buttonPanel, null, "Mysoftwarefm", false, new Runnable() {
						@Override
						public void run() {
							explorer.showMySoftwareFm();
						}
					});

					Swts.Row.setRowDataFor(100, 20, buttonPanel.getChildren());
					explorer.addExplorerListener(IExplorerListener.Utils.sysout());
					return explorerAndButton;
				}
			});
		} finally {
			serviceExecutor.shutdownAndAwaitTermination(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
			ICrowdSourcedApi api = forShutdown.get();
			if (api != null)
				api.shutdown();
		}

	}
}