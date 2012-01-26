/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.collections.explorer.internal;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
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
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.card.ILineSelectedListener;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.CardAndCollectionDataStoreAdapter;
import org.softwareFm.card.dataStore.ICardDataStore;
import org.softwareFm.collections.ICollectionConfigurationFactory;
import org.softwareFm.collections.explorer.ExplorerAdapter;
import org.softwareFm.collections.explorer.IExplorer;
import org.softwareFm.collections.explorer.IUsageStrategy;
import org.softwareFm.collections.explorer.SnippetFeedConfigurator;
import org.softwareFm.collections.mySoftwareFm.ILoginStrategy;
import org.softwareFm.display.browser.IBrowserConfigurator;
import org.softwareFm.display.swt.SwtAndServiceTest;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.display.timeline.IPlayListGetter;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.server.GitRepositoryFactory;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.AbstractLoginDataAccessor;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.runnable.Callables;
import org.softwareFm.utilities.strings.Strings;
import org.softwareFm.utilities.tests.INeedsServerTest;
import org.softwareFm.utilities.tests.Tests;
import org.softwareFm.utilities.url.Urls;

/** These tests go out to software fm, so they are much more fragile */
abstract public class AbstractExplorerIntegrationTest extends SwtAndServiceTest implements INeedsServerTest {

	final static String groupUrl = "/ant";
	final static String artifactUrl = "/ant/ant/artifact/ant";
	final static String snippetrepoUrl = "/java/io/File";
	final static String snippetUrl = "snippet/java/io/File/snippet";

	protected CardConfig cardConfig;
	protected IRepositoryFacard repository;
	public Explorer explorer;
	protected IHttpClient httpClient;
	public final long delay = 10000;
	protected MasterDetailSocial masterDetailSocial;

	protected final String prefix = "/tests/" + getClass().getSimpleName();
	protected final String rootArtifactUrl = prefix + "/data";
	protected final String rootSnippetUrl = prefix + "/snippet";
	protected IResourceGetter rawResourceGetter;
	private File root;
	protected File localRoot;
	protected File remoteRoot;

	// private String remoteAsUri;

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
				dispatchUntilTimeoutOrLatch(latch, delay);
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
		dispatchUntilTimeoutOrLatch(latch, delay);
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
		// remoteAsUri = new File(root, "remote").getAbsolutePath();

		httpClient = IHttpClient.Utils.builder("localhost", ServerConstants.testPort);
		repository = GitRepositoryFactory.gitLocalRepositoryFacardWithServer(AbstractLoginDataAccessor.defaultDataSource(), ServerConstants.testPort, localRoot, remoteRoot, Functions.<Map<String, Object>, String> expectionIfCalled(), Callables.<String> exceptionIfCalled(), Callables.<String> exceptionIfCalled(), Callables.<Integer> exceptionIfCalled(), Callables.<String> exceptionIfCalled(), "g", "a");

		try {
			cardConfig = ICollectionConfigurationFactory.Utils.softwareFmConfigurator().//
					configure(display, new CardConfig(ICardFactory.Utils.cardFactory(), ICardDataStore.Utils.repositoryCardDataStore(shell, repository))).//
					withUrlGeneratorMap(ICollectionConfigurationFactory.Utils.makeSoftwareFmUrlGeneratorMap(prefix, "data"));
			masterDetailSocial = new MasterDetailSocial(shell, SWT.NULL);
			IUsageStrategy usageStrategy = IUsageStrategy.Utils.usage(service, httpClient, IGitServer.Utils.noGitServer(), ServerConstants.userGenerator(), ServerConstants.projectGenerator(), "g", "a");
			explorer = (Explorer) IExplorer.Utils.explorer(masterDetailSocial, cardConfig, Arrays.asList(rootArtifactUrl, rootSnippetUrl), IPlayListGetter.Utils.noPlayListGetter(), service, ILoginStrategy.Utils.noLoginStrategy(), usageStrategy);
			IBrowserConfigurator.Utils.configueWithUrlRssTweet(explorer);
			SnippetFeedConfigurator.configure(explorer, cardConfig);
			httpClient.delete(Urls.compose(rootArtifactUrl, artifactUrl)).execute(IResponseCallback.Utils.noCallback()).get();
			httpClient.delete(Urls.compose(rootArtifactUrl, snippetUrl)).execute(IResponseCallback.Utils.noCallback()).get();
			repository.makeRoot(Urls.compose(rootArtifactUrl, groupUrl), IResponseCallback.Utils.noCallback()).get();
			rawResourceGetter = explorer.getCardConfig().resourceGetterFn.apply(null);
		} catch (Exception e) {
			repository.shutdown();// otherwise many future tests get BindException
			throw e;
		}
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		repository.shutdown();// also closes httpclient
		explorer.dispose();
		cardConfig.dispose();
		Tests.waitUntilCanDeleteTempDirectory(getClass().getSimpleName(), 2000);
	}

	protected void postSnippetData() {
		try {
			repository.makeRoot(Urls.compose(rootSnippetUrl, snippetrepoUrl), IResponseCallback.Utils.noCallback()).get();
			repository.post(Urls.compose(rootSnippetUrl, snippetUrl), Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.collection), IResponseCallback.Utils.noCallback()).get();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	protected void postArtifactData() {
		try {
			repository.post(Urls.compose(rootArtifactUrl, artifactUrl), Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.artifact), IResponseCallback.Utils.noCallback()).get();
			repository.post(Urls.compose(rootArtifactUrl, artifactUrl, "/tutorial"), Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.collection), IResponseCallback.Utils.noCallback()).get();
			repository.post(Urls.compose(rootArtifactUrl, artifactUrl, "/tutorial/one"), Maps.stringObjectMap(CardConstants.slingResourceType, "tutorial"), IResponseCallback.Utils.noCallback()).get();
			repository.post(Urls.compose(rootArtifactUrl, artifactUrl, "/tutorial/two"), Maps.stringObjectMap(CardConstants.slingResourceType, "tutorial"), IResponseCallback.Utils.noCallback()).get();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
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