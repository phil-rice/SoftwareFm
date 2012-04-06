/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.usage.internal;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.Assert;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.IContainerBuilder;
import org.softwareFm.crowdsource.api.ICrowdSourcedServer;
import org.softwareFm.crowdsource.api.IExtraCallProcessorFactory;
import org.softwareFm.crowdsource.api.IExtraReaderWriterConfigurator;
import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
import org.softwareFm.crowdsource.api.LocalConfig;
import org.softwareFm.crowdsource.api.ServerConfig;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitLocal;
import org.softwareFm.crowdsource.api.git.IGitWriter;
import org.softwareFm.crowdsource.api.server.ICallProcessor;
import org.softwareFm.crowdsource.api.user.IGroupOperations;
import org.softwareFm.crowdsource.httpClient.IHttpClient;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.utilities.arrays.ArrayHelper;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback2;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.resources.IResourceGetter;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.crowdsource.utilities.tests.INeedsServerTest;
import org.softwareFm.crowdsource.utilities.tests.Tests;
import org.softwareFm.crowdsource.utilities.url.Urls;
import org.softwareFm.eclipse.mysoftwareFm.MyDetails;
import org.softwareFm.eclipse.mysoftwareFm.MyGroups;
import org.softwareFm.eclipse.mysoftwareFm.MyPeople;
import org.softwareFm.eclipse.snippets.SnippetFeedConfigurator;
import org.softwareFm.jarAndClassPath.api.IProjectTimeGetter;
import org.softwareFm.jarAndClassPath.api.IRequestGroupReportGeneration;
import org.softwareFm.jarAndClassPath.api.ISoftwareFmApiFactory;
import org.softwareFm.jarAndClassPath.api.IUsageReader;
import org.softwareFm.jarAndClassPath.api.IUserDataManager;
import org.softwareFm.jarAndClassPath.api.ProjectFixture;
import org.softwareFm.jarAndClassPath.api.ProjectMock;
import org.softwareFm.jarAndClassPath.api.ProjectTimeGetterFixture;
import org.softwareFm.swt.ICollectionConfigurationFactory;
import org.softwareFm.swt.browser.IBrowserConfigurator;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.card.ICardHolder;
import org.softwareFm.swt.card.ILineSelectedListener;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.dataStore.CardAndCollectionDataStoreAdapter;
import org.softwareFm.swt.dataStore.ICardDataStore;
import org.softwareFm.swt.explorer.ExplorerAdapter;
import org.softwareFm.swt.explorer.IExplorer;
import org.softwareFm.swt.explorer.IShowMyData;
import org.softwareFm.swt.explorer.IShowMyGroups;
import org.softwareFm.swt.explorer.IShowMyPeople;
import org.softwareFm.swt.explorer.internal.Explorer;
import org.softwareFm.swt.explorer.internal.MasterDetailSocial;
import org.softwareFm.swt.login.ILoginStrategy;
import org.softwareFm.swt.swt.Swts;
import org.softwareFm.swt.timeline.IPlayListGetter;

/** These tests go out to software fm, so they are much more fragile */
abstract public class AbstractExplorerIntegrationTest extends ApiAndSwtTest implements INeedsServerTest {
	protected final static String groupUrl = "/ant";
	protected final static String artifactUrl = "/ant/ant/artifact/ant";
	protected final static String snippetrepoUrl = "/java/io/File";
	protected final static String snippetUrl = "/snippet/java/io/File/snippet";

	protected CardConfig cardConfig;
	public Explorer explorer;
	protected MasterDetailSocial masterDetailSocial;

	protected final String prefix = "/tests/" + getClass().getSimpleName();
	protected final String rootArtifactUrl = prefix + "/data";
	protected final String rootSnippetUrl = prefix + "/snippet";
	protected IResourceGetter rawResourceGetter;

	private IShowMyData showMyData;
	protected IUserDataManager userDataManager;

	public static interface CardHolderAndCardCallback {
		void process(ICardHolder cardHolder, ICard card) throws Exception;
	}

	static interface IAddingCallback<T> {
		/** will get called twice. If added is false, card is the initial card, if false card is the added card */
		void process(boolean added, T data, IAdding adding);
	}

	static interface IAdding {
		void tableItem(int index, String name, String existing, String newValue);
	}

	protected void displayCard(final String url, final CardHolderAndCardCallback cardHolderAndCardCallback) {
		try {
			ICard card = explorer.displayCard(rootArtifactUrl + url, new CardAndCollectionDataStoreAdapter() ).get();
			cardHolderAndCardCallback.process(explorer.getCardHolder(), card);
			dispatchUntilJobsFinished();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	protected void displayCardThenViewChild(String url, final String childTitle, final CardHolderAndCardCallback callback) {
		final AtomicInteger count = new AtomicInteger();
		displayCard(url, new CardHolderAndCardCallback() {
			@Override
			public void process(ICardHolder cardHolder, ICard card) throws Exception {
				System.out.println("in displaceCardThenViewChild");
				final CountDownLatch latch = new CountDownLatch(1);
				cardHolder.addLineSelectedListener(new ILineSelectedListener() {
					@Override
					public void selected(ICard card, String key, Object value) {
						if (latch.getCount() == 0)
							fail("Card: " + card + "\nKey: " + key + " value: " + value);
						latch.countDown();
					}
				});
				Menu menu = selectAndCreatePopupMenu(card, childTitle);
				executeMenuItem(menu, "View");
				// this should have called "explorer.showContents
				// Now wait for the menu to have "done it's thing"
				dispatchUntilTimeoutOrLatch(latch);
				ICard childCard = cardHolder.getCard();
				callback.process(cardHolder, childCard);
				assertEquals(1, count.incrementAndGet());
			}

		});
		assertEquals(1, count.get());// check the body was called
	}

	protected ICard doSomethingAndWaitForCardDataStoreToFinish(Runnable something, final CardHolderAndCardCallback cardHolderAndCardCallback) {
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<ICard> cardRef = new AtomicReference<ICard>();
		final AtomicReference<Exception> exception = new AtomicReference<Exception>();
		explorer.addExplorerListener(new ExplorerAdapter() {
			@Override
			public void finished(ICardHolder cardHolder, String url, ICard card) throws Exception {
				try {
					explorer.removeExplorerListener(this);
					cardHolderAndCardCallback.process(cardHolder, card);
					dispatchUntilJobsFinished();
					cardRef.set(card);
				} catch (Exception e) {
					exception.set(e);
					throw e;
				} finally {
					latch.countDown();
				}
			}
		});
		dispatchUntilJobsFinished();
		something.run();
		dispatchUntilTimeoutOrLatch(latch);
		if (exception.get() != null)
			throw new RuntimeException(exception.get());
		return cardRef.get();
	}

	protected void executeMenuItem(Menu menu, String title) {
		for (MenuItem item : menu.getItems()) {
			if (item.getText().equals(title)) {
				item.notifyListeners(SWT.Selection, new Event());
				return;
			}
		}
		throw new IllegalArgumentException(title + "\n" + menu);
	}

	protected Menu selectAndCreatePopupMenu(ICard card, String title) {
		selectItem(card, title);
		return createPopupMenu(card);
	}

	protected Menu createPopupMenu(ICard card) {
		final Menu menu = new Menu(shell);
		String popupMenuId = Functions.call(card.getCardConfig().popupMenuIdFn, card);
		cardConfig.popupMenuService.contributeTo(popupMenuId, new Event(), menu, card);
		return menu;
	}

	protected void selectItemAndNotifyListeners(ICard card, String title) {
		selectItem(card, title);
		card.getTable().notifyListeners(SWT.Selection, new Event());
	}

	protected void selectItem(ICard card, String title) {
		Table table = card.getTable();
		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);
			if (item.getText(0).equals(title)) {
				table.select(i);
				return;
			}
		}
		Assert.fail("Cannot find: " + title + "\nCard: " + card);
	}

	protected void checkTable(Table table, int i, String name, String value) {
		assertEquals(name, table.getItem(i).getText(0));
		assertEquals(value, table.getItem(i).getText(1));

	}

	@Override
	protected IExtraCallProcessorFactory getExtraProcessCalls() {
		final IExtraCallProcessorFactory parent = super.getExtraProcessCalls();
		return new IExtraCallProcessorFactory() {
			@Override
			public ICallProcessor[] makeExtraCalls(IContainer api, ServerConfig serverConfig) {
				ICallProcessor[] parentCalls = parent.makeExtraCalls(api, serverConfig);
				ICallProcessor[] softwareFmCalls = ISoftwareFmApiFactory.Utils.getExtraProcessCalls().makeExtraCalls(api, serverConfig);
				return ArrayHelper.append(parentCalls, softwareFmCalls);
			}
		};
	}

	@Override
	protected IExtraReaderWriterConfigurator<ServerConfig> getServerExtraReaderWriterConfigurator() {
		return new IExtraReaderWriterConfigurator<ServerConfig>() {
			@Override
			public void builder(IContainerBuilder builder, ServerConfig apiConfig) {
				ISoftwareFmApiFactory.Utils.getServerExtraReaderWriterConfigurator(getUrlPrefix(), apiConfig.timeOutMs).builder(builder, apiConfig);
				ProjectMock projectMock = getProjectTimeGetterFixture(builder);
				builder.register(IUsageReader.class, projectMock);
			}
		};
	}

	protected ProjectMock getProjectTimeGetterFixture(IContainerBuilder builder) {
		IProjectTimeGetter projectTimeGetter = new ProjectTimeGetterFixture();
		builder.register(IProjectTimeGetter.class, projectTimeGetter);
		ProjectMock projectMock = new ProjectMock(false);
		projectMock.register("someNewSoftwareFmId0", projectCryptoKey0, ProjectFixture.map0);
		projectMock.register("someNewSoftwareFmId1", projectCryptoKey1, ProjectFixture.map1);
		return projectMock;
	}

	@Override
	protected IExtraReaderWriterConfigurator<LocalConfig> getLocalExtraReaderWriterConfigurator() {
		final IExtraReaderWriterConfigurator<LocalConfig> parent = super.getLocalExtraReaderWriterConfigurator();
		return new IExtraReaderWriterConfigurator<LocalConfig>() {
			@Override
			public void builder(IContainerBuilder builder, LocalConfig localConfig) {
				parent.builder(builder, localConfig);
				IProjectTimeGetter projectTimeGetter = new ProjectTimeGetterFixture();
				IRequestGroupReportGeneration requestGroupReportGeneration = IRequestGroupReportGeneration.Utils.httpClient(builder, IResponseCallback.Utils.noCallback());
				IGroupOperations clientGroupOperations = IGroupOperations.Utils.clientGroupOperations(builder, localConfig.timeOutMs);

				builder.register(IProjectTimeGetter.class, projectTimeGetter);
				builder.register(IRequestGroupReportGeneration.class, requestGroupReportGeneration);
				builder.register(IUsageReader.class, IUsageReader.Utils.localUsageReader(builder, localConfig.userUrlGenerator));
				builder.register(IGroupOperations.class, clientGroupOperations);
			}

		};
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardConfig = ICollectionConfigurationFactory.Utils.softwareFmConfigurator().//
				configure(display, new CardConfig(ICardFactory.Utils.cardFactory(), ICardDataStore.Utils.repositoryCardDataStore(shell, getLocalApi().makeContainer()))).//
				withUrlGeneratorMap(ICollectionConfigurationFactory.Utils.makeSoftwareFmUrlGeneratorMap(prefix, "data"));
		ICrowdSourcedServer server = getServerApi().getServer();
		IUserAndGroupsContainer container = getLocalApi().makeUserAndGroupsContainer();
		try {
			rawResourceGetter = cardConfig.resourceGetterFn.apply(null);
			masterDetailSocial = new MasterDetailSocial(shell, SWT.NULL);
			showMyData = MyDetails.showMyDetails(container, cardConfig, masterDetailSocial);
			IShowMyGroups showMyGroups = MyGroups.showMyGroups(masterDetailSocial, container, false, cardConfig);
			IShowMyPeople showMyPeople = MyPeople.showMyPeople(container, masterDetailSocial, cardConfig, 2 * getLocalConfig().timeOutMs);

			userDataManager = IUserDataManager.Utils.userDataManager();
			explorer = (Explorer) IExplorer.Utils.explorer(masterDetailSocial, container, cardConfig, //
					Arrays.asList(rootArtifactUrl, rootSnippetUrl), //
					IPlayListGetter.Utils.noPlayListGetter(), //
					ILoginStrategy.Utils.noLoginStrategy(), showMyData,//
					showMyGroups, showMyPeople, userDataManager,//
					Callables.<Long> exceptionIfCalled());
			IBrowserConfigurator.Utils.configueWithUrlRssTweet(explorer);
			new SnippetFeedConfigurator(cardConfig.cardDataStore, cardConfig.resourceGetterFn).configure(explorer);
			// SnippetFeedConfigurator.configure(explorer, cardConfig);
			getLocalContainer().access(IHttpClient.class, IGitLocal.class, new ICallback2<IHttpClient, IGitLocal>() {
				@Override
				public void process(IHttpClient httpClient, IGitLocal gitLocal) throws Exception {
					httpClient.delete(Urls.compose(rootArtifactUrl, artifactUrl)).execute(IResponseCallback.Utils.noCallback()).get();
					httpClient.delete(Urls.compose(rootArtifactUrl, snippetUrl)).execute(IResponseCallback.Utils.noCallback()).get();
					gitLocal.init(Urls.compose(rootArtifactUrl, groupUrl), "setup");
				}
			}).get();
		} catch (Exception e) {
			server.shutdown();
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		masterDetailSocial.dispose();
		explorer.dispose();
		cardConfig.dispose();
		dataSource.close();
		Tests.waitUntilCanDeleteTempDirectory(getClass().getSimpleName(), 2000);

	}

	protected void postSnippetData() {
		final String url = Urls.compose(rootSnippetUrl, snippetUrl);
		getLocalApi().makeContainer().access(IGitWriter.class, new ICallback<IGitWriter>() {
			@Override
			public void process(IGitWriter gitWriter) throws Exception {
				gitWriter.put(IFileDescription.Utils.plain(url), Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.collection), "postSnippetData");
			}
		}).get();
	}

	protected void postArtifactData() {
		getLocalApi().makeContainer().access(IGitWriter.class, new ICallback<IGitWriter>() {
			@Override
			public void process(IGitWriter gitWriter) throws Exception {
				gitWriter.put(IFileDescription.Utils.plain(Urls.compose(rootArtifactUrl, artifactUrl)), Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.artifact), "postArtefactData_0");
				gitWriter.put(IFileDescription.Utils.plain(Urls.compose(rootArtifactUrl, artifactUrl, "/tutorial")), Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.collection), "postArtefactData_1");
				gitWriter.put(IFileDescription.Utils.plain(Urls.compose(rootArtifactUrl, artifactUrl, "/tutorial/one")), Maps.stringObjectMap(CardConstants.slingResourceType, "tutorial"), "postArtefactData_2");
				gitWriter.put(IFileDescription.Utils.plain(Urls.compose(rootArtifactUrl, artifactUrl, "/tutorial/two")), Maps.stringObjectMap(CardConstants.slingResourceType, "tutorial"), "postArtefactData_3");
			}
		}).get();
	}

	protected void checkAndEdit(final Table cardTable, IAddingCallback<Table> addingCallback) {
		addingCallback.process(false, cardTable, new IAdding() {
			@Override
			public void tableItem(int index, String name, String existing, String newValue) {
				String prettyName = Strings.camelCaseToPretty(name);
				TableItem item = cardTable.getItem(index);
				assertEquals(prettyName, item.getText(0));
				assertEquals(existing, item.getText(1));
				cardTable.select(index);
				cardTable.notifyListeners(SWT.Selection, new Event());
				Text text = Swts.findChildrenWithClass(cardTable, Text.class).get(index);
				assertEquals(existing, text.getText());
				text.setText(newValue);
			}
		});
	}
}