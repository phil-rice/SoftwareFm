/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.usage.internal;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.Assert;

import org.apache.commons.dbcp.BasicDataSource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.client.gitwriter.HttpGitWriter;
import org.softwareFm.client.gitwriter.HttpRepoFinder;
import org.softwareFm.client.http.api.IHttpClient;
import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.exceptions.WrappedException;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.processors.AbstractLoginDataAccessor;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.tests.INeedsServerTest;
import org.softwareFm.common.tests.Tests;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.common.url.Urls;
import org.softwareFm.eclipse.IRequestGroupReportGeneration;
import org.softwareFm.eclipse.mysoftwareFm.MyDetails;
import org.softwareFm.eclipse.mysoftwareFm.MyGroups;
import org.softwareFm.eclipse.mysoftwareFm.RequestGroupReportGeneration;
import org.softwareFm.eclipse.snippets.SnippetFeedConfigurator;
import org.softwareFm.eclipse.user.IProjectTimeGetter;
import org.softwareFm.eclipse.user.ProjectTimeGetterFixture;
import org.softwareFm.server.ICrowdSourcedServer;
import org.softwareFm.server.processors.IMailer;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.ProcessCallParameters;
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
import org.softwareFm.swt.explorer.internal.Explorer;
import org.softwareFm.swt.explorer.internal.MasterDetailSocial;
import org.softwareFm.swt.mySoftwareFm.ILoginStrategy;
import org.softwareFm.swt.swt.SwtAndServiceTest;
import org.softwareFm.swt.swt.Swts;
import org.softwareFm.swt.timeline.IPlayListGetter;

/** These tests go out to software fm, so they are much more fragile */
abstract public class AbstractExplorerIntegrationTest extends SwtAndServiceTest implements INeedsServerTest {

	final static String groupUrl = "/ant";
	final static String artifactUrl = "/ant/ant/artifact/ant";
	final static String snippetrepoUrl = "/java/io/File";
	final static String snippetUrl = "/snippet/java/io/File/snippet";

	protected CardConfig cardConfig;
	protected IGitLocal gitLocal;
	public Explorer explorer;
	protected IHttpClient httpClient;
	protected MasterDetailSocial masterDetailSocial;

	protected final String prefix = "/tests/" + getClass().getSimpleName();
	protected final String rootArtifactUrl = prefix + "/data";
	protected final String rootSnippetUrl = prefix + "/snippet";
	protected IResourceGetter rawResourceGetter;
	private File root;
	protected File localRoot;
	protected File remoteRoot;
	protected String userCryptoKey;
	private ICrowdSourcedServer crowdSourcedServer;
	private IShowMyData showMyData;

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
		doSomethingAndWaitForCardDataStoreToFinish(new Runnable() {
			@Override
			public void run() {
				explorer.displayCard(rootArtifactUrl + url, new CardAndCollectionDataStoreAdapter());
			}
		}, cardHolderAndCardCallback);

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
				dispatchUntilTimeoutOrLatch(latch, CommonConstants.testTimeOutMs);
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
					dispatchUntilQueueEmpty();
					cardRef.set(card);
				} catch (Exception e) {
					exception.set(e);
					throw e;
				} finally {
					latch.countDown();
				}
			}
		});
		something.run();
		dispatchUntilTimeoutOrLatch(latch, CommonConstants.testTimeOutMs);
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
	protected void setUp() throws Exception {
		super.setUp();
		root = Tests.makeTempDirectory(getClass().getSimpleName());
		localRoot = new File(root, "local");
		remoteRoot = new File(root, "remote");
		String remoteAsUri = new File(root, "remote").getAbsolutePath();

		httpClient = IHttpClient.Utils.builder("localhost", CommonConstants.testPort);

		gitLocal = IGitLocal.Utils.localReader(//
				new HttpRepoFinder(httpClient, CommonConstants.testTimeOutMs), //
				IGitOperations.Utils.gitOperations(localRoot), //
				new HttpGitWriter(httpClient, CommonConstants.testTimeOutMs), remoteAsUri, CommonConstants.staleCachePeriodForTest);
		BasicDataSource dataSource = AbstractLoginDataAccessor.defaultDataSource();

		Callable<String> cryptoGenerator = Callables.value(userCryptoKey);
		Callable<String> softwareFmIdGenerator = Callables.patternWithCount("newSoftwareFmId{0}");

		IGitOperations remoteGitOperations = IGitOperations.Utils.gitOperations(remoteRoot);
		IFunction1<Map<String, Object>, String> cryptoFn = ICrowdSourcedServer.Utils.cryptoFn(dataSource);
		ProcessCallParameters processCallParameters = new ProcessCallParameters(dataSource, remoteGitOperations, cryptoGenerator, softwareFmIdGenerator, cryptoFn, IMailer.Utils.noMailer());
		IProcessCall processCall = IProcessCall.Utils.softwareFmProcessCall(processCallParameters, Functions.<ProcessCallParameters, IProcessCall[]> constant(new IProcessCall[0]));
		crowdSourcedServer = ICrowdSourcedServer.Utils.testServerPort(processCall, ICallback.Utils.rethrow());

		try {
			cardConfig = ICollectionConfigurationFactory.Utils.softwareFmConfigurator().//
					configure(display, new CardConfig(ICardFactory.Utils.cardFactory(), ICardDataStore.Utils.repositoryCardDataStore(shell, service, gitLocal))).//
					withUrlGeneratorMap(ICollectionConfigurationFactory.Utils.makeSoftwareFmUrlGeneratorMap(prefix, "data"));
			rawResourceGetter = cardConfig.resourceGetterFn.apply(null);
			masterDetailSocial = new MasterDetailSocial(shell, SWT.NULL);
			IProjectTimeGetter projectTimeGetter = new ProjectTimeGetterFixture();
			IUrlGenerator userUrlGenerator = cardConfig.urlGeneratorMap.get(CardConstants.userUrlKey);
			IUrlGenerator groupUrlGenerator = GroupConstants.groupsGenerator();
			showMyData = MyDetails.showMyDetails(service, cardConfig, masterDetailSocial, userUrlGenerator, gitLocal, projectTimeGetter);
			
			IRequestGroupReportGeneration reportGenerator = new RequestGroupReportGeneration(httpClient, IResponseCallback.Utils.sysoutStatusCallback());
			IShowMyGroups showMyGroups = MyGroups.showMyGroups(service, cardConfig, masterDetailSocial, userUrlGenerator,groupUrlGenerator, gitLocal, projectTimeGetter, reportGenerator);
			explorer = (Explorer) IExplorer.Utils.explorer(masterDetailSocial, cardConfig, //
					Arrays.asList(rootArtifactUrl, rootSnippetUrl), //
					IPlayListGetter.Utils.noPlayListGetter(), service, //
					ILoginStrategy.Utils.noLoginStrategy(),//
					showMyData, showMyGroups);
			IBrowserConfigurator.Utils.configueWithUrlRssTweet(explorer);
			new SnippetFeedConfigurator(cardConfig.cardDataStore, cardConfig.resourceGetterFn).configure(explorer);
			// SnippetFeedConfigurator.configure(explorer, cardConfig);
			httpClient.delete(Urls.compose(rootArtifactUrl, artifactUrl)).execute(IResponseCallback.Utils.noCallback()).get();
			httpClient.delete(Urls.compose(rootArtifactUrl, snippetUrl)).execute(IResponseCallback.Utils.noCallback()).get();
			gitLocal.init(Urls.compose(rootArtifactUrl, groupUrl));
		} catch (Exception e) {
			crowdSourcedServer.shutdown();
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			super.tearDown();
			masterDetailSocial.dispose();
			explorer.dispose();
			cardConfig.dispose();
		} finally {
			crowdSourcedServer.shutdown();
		}
		Tests.waitUntilCanDeleteTempDirectory(getClass().getSimpleName(), 2000);

	}

	protected void postSnippetData() {
		try {
			String url = Urls.compose(rootSnippetUrl, snippetUrl);
			gitLocal.put(IFileDescription.Utils.plain(url), Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.collection));
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	protected void postArtifactData() {
		gitLocal.put(IFileDescription.Utils.plain(Urls.compose(rootArtifactUrl, artifactUrl)), Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.artifact));
		gitLocal.put(IFileDescription.Utils.plain(Urls.compose(rootArtifactUrl, artifactUrl, "/tutorial")), Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.collection));
		gitLocal.put(IFileDescription.Utils.plain(Urls.compose(rootArtifactUrl, artifactUrl, "/tutorial/one")), Maps.stringObjectMap(CardConstants.slingResourceType, "tutorial"));
		gitLocal.put(IFileDescription.Utils.plain(Urls.compose(rootArtifactUrl, artifactUrl, "/tutorial/two")), Maps.stringObjectMap(CardConstants.slingResourceType, "tutorial"));
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