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
import org.softwareFm.client.gitwriter.HttpGitWriter;
import org.softwareFm.client.gitwriter.HttpRepoFinder;
import org.softwareFm.client.http.api.IHttpClient;
import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.LocalGroupsReader;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.services.IServiceExecutor;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.IRequestGroupReportGeneration;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.mysoftwareFm.MyDetails;
import org.softwareFm.eclipse.mysoftwareFm.MyGroups;
import org.softwareFm.eclipse.mysoftwareFm.MyPeople;
import org.softwareFm.eclipse.mysoftwareFm.RequestGroupReportGeneration;
import org.softwareFm.eclipse.snippets.SnippetFeedConfigurator;
import org.softwareFm.eclipse.user.IProjectTimeGetter;
import org.softwareFm.eclipse.user.IUserMembershipReader;
import org.softwareFm.eclipse.user.UserMembershipReaderForLocal;
import org.softwareFm.swt.ICollectionConfigurationFactory;
import org.softwareFm.swt.browser.IBrowserConfigurator;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardChangedListener;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.card.ICardHolder;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
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
import org.softwareFm.swt.explorer.IUserDataManager;
import org.softwareFm.swt.menu.ICardMenuItemHandler;
import org.softwareFm.swt.mySoftwareFm.ILoginStrategy;
import org.softwareFm.swt.swt.Swts;
import org.softwareFm.swt.swt.Swts.Buttons;
import org.softwareFm.swt.swt.Swts.Grid;
import org.softwareFm.swt.timeline.IPlayListGetter;

public class ExplorerWithRadioChannel {
	public static void main(String[] args) {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ERROR);

		// Logger.getLogger(IGitLocal.class).setLevel(Level.DEBUG);
		Logger.getLogger(IHttpClient.class).setLevel(Level.DEBUG);
		File home = new File(System.getProperty("user.home"));
		final File localRoot = new File(home, ".sfm");
		boolean local = true;
		String server = local ? "localhost" : SoftwareFmConstants.softwareFmServerUrl;
		String prefix = local ? new File(home, ".sfm_remote").getAbsolutePath() : SoftwareFmConstants.gitProtocolAndGitServerName;
		// String prefix = "git://localhost:7777/";
		int port = local ? 8080 : 80;
		final IHttpClient client = IHttpClient.Utils.builder(server, port);
		final IGitOperations gitOperations = IGitOperations.Utils.gitOperations(localRoot);
		final IGitLocal gitLocal = IGitLocal.Utils.localReader(new HttpRepoFinder(client, CommonConstants.clientTimeOut), gitOperations, new HttpGitWriter(client), prefix, CommonConstants.staleCachePeriodForTest);

		final IServiceExecutor service = IServiceExecutor.Utils.defaultExecutor();
		try {
			final List<String> rootUrl = Arrays.asList("/softwareFm/data", "/softwareFm/snippet");
			final String firstUrl = "/softwareFm/data/ant/ant/artifact/ant";
			Swts.Show.display(ExplorerWithRadioChannel.class.getSimpleName(), new IFunction1<Composite, Composite>() {
				@Override
				public Composite apply(Composite from) throws Exception {
					Composite explorerAndButton = Swts.newComposite(from, SWT.NULL, "ExplorerAndButton");
					explorerAndButton.setLayout(new GridLayout());
					Composite buttonPanel = Swts.newComposite(explorerAndButton, SWT.NULL, "ButtonPanel");
					buttonPanel.setLayout(Swts.Row.getHorizonalNoMarginRowLayout());

					final IMutableCardDataStore cardDataStore = ICardDataStore.Utils.repositoryCardDataStore(from, service, gitLocal);
					ICardFactory cardFactory = ICardFactory.Utils.cardFactory();
					final CardConfig cardConfig = ICollectionConfigurationFactory.Utils.softwareFmConfigurator().configure(from.getDisplay(), new CardConfig(cardFactory, cardDataStore));
					IMasterDetailSocial masterDetailSocial = IMasterDetailSocial.Utils.masterDetailSocial(explorerAndButton);
					IPlayListGetter playListGetter = new ArtifactPlayListGetter(cardDataStore);
					ILoginStrategy loginStrategy = ILoginStrategy.Utils.softwareFmLoginStrategy(from.getDisplay(), service, client);
					final IProjectTimeGetter projectTimeGetter = IProjectTimeGetter.Utils.timeGetter();
					IUrlGenerator userUrlGenerator = cardConfig.urlGeneratorMap.get(CardConstants.userUrlKey);
					IUrlGenerator groupUrlGenerator = GroupConstants.groupsGenerator(SoftwareFmConstants.urlPrefix);
					IRequestGroupReportGeneration requestGroupReportGenerator = new RequestGroupReportGeneration(client, IResponseCallback.Utils.sysoutStatusCallback());
					IShowMyData showMyDetails = MyDetails.showMyDetails(service, cardConfig, masterDetailSocial, userUrlGenerator, gitLocal, projectTimeGetter);
					IShowMyGroups showMyGroups = MyGroups.showMyGroups(service, cardConfig, masterDetailSocial, userUrlGenerator, groupUrlGenerator, gitLocal, projectTimeGetter, requestGroupReportGenerator);
					IShowMyPeople showMyPeople = MyPeople.showMyPeople(service, masterDetailSocial, cardConfig, gitLocal, userUrlGenerator, groupUrlGenerator, projectTimeGetter, requestGroupReportGenerator, CommonConstants.clientTimeOut);
					IUserReader userReader= IUserReader.Utils.localUserReader(gitLocal, userUrlGenerator);
					IUserMembershipReader userMembershipReader = new UserMembershipReaderForLocal(userUrlGenerator, gitLocal, userReader);
					LocalGroupsReader groupsReader = new LocalGroupsReader(groupUrlGenerator, gitLocal);

					final IExplorer explorer = IExplorer.Utils.explorer(masterDetailSocial, userReader, userMembershipReader, groupsReader, cardConfig, rootUrl, playListGetter, service, loginStrategy, showMyDetails, showMyGroups, showMyPeople, IUserDataManager.Utils.userDataManager());

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
			service.shutdownAndAwaitTermination(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
			IServiceExecutor.Utils.defaultExecutor().shutdownAndAwaitTermination(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
			client.shutdown();
		}

	}
}