package org.softwareFm.collections.explorer.internal;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.card.ILineSelectedListener;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.CardAndCollectionDataStoreAdapter;
import org.softwareFm.card.dataStore.ICardDataStore;
import org.softwareFm.collections.ICollectionConfigurationFactory;
import org.softwareFm.collections.constants.CollectionConstants;
import org.softwareFm.collections.explorer.IExplorer;
import org.softwareFm.collections.explorer.IExplorerListener;
import org.softwareFm.collections.explorer.IMasterDetailSocial;
import org.softwareFm.display.browser.IBrowserConfigurator;
import org.softwareFm.display.swt.SwtAndServiceTest;
import org.softwareFm.display.timeline.IPlayListGetter;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.constants.HttpClientConstants;
import org.softwareFm.repositoryFacard.impl.RepositoryFacard;
import org.softwareFm.utilities.exceptions.WrappedException;

/** These tests go out to software fm, so they are much more fragile */
abstract public class AbstractExplorerIntegrationTest extends SwtAndServiceTest {

	protected CardConfig cardConfig;
	protected RepositoryFacard repository;
	protected IExplorer explorer;
	protected IHttpClient httpClient;
	protected final long delay = 2000;

	public static interface CardHolderAndCardCallback {
		void process(ICardHolder cardHolder, ICard card) throws Exception;
	}

	protected void displayCard(String url, final CardHolderAndCardCallback cardHolderAndCardCallback) {
		try {
			final AtomicInteger count = new AtomicInteger();
			final CountDownLatch latch = new CountDownLatch(1);
			explorer.addExplorerListener(IExplorerListener.Utils.sysout());
			explorer.displayCard(CollectionConstants.rootUrl + url, new CardAndCollectionDataStoreAdapter() {
				@Override
				public void finished(ICardHolder cardHolder, String url, ICard card) throws Exception {
					System.out.println("In finished");
					assertEquals(1, count.incrementAndGet());
					dispatchUntilQueueEmpty();
					cardHolderAndCardCallback.process(cardHolder, card);
					latch.countDown();
				}
			});
			dispatchUntilTimeoutOrLatch(latch, delay);
			assertEquals(1, count.get());
		} catch (InterruptedException e) {
			throw WrappedException.wrap(e);
		}
	}

	protected void displayCardThenViewChild(String url, final String childTitle, final CardHolderAndCardCallback callback) {
		final AtomicInteger count = new AtomicInteger();
		displayCard(url, new CardHolderAndCardCallback() {
			@Override
			public void process(ICardHolder cardHolder, ICard card) throws Exception {
				final CountDownLatch latch = new CountDownLatch(1);
				card.addLineSelectedListener(new ILineSelectedListener() {
					@Override
					public void selected(ICard card, String key, Object value) {
						if (latch.getCount() == 0)
							fail("Card: " + card + "\nKey: " + key + " value: " + value);
						latch.countDown();
					}
				});
				Menu menu = popupMenuForTitle(card, childTitle);
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

	protected void executeMenuItem(Menu menu, String title) {
		for (MenuItem item : menu.getItems()) {
			if (item.getText().equals(title)) {
				item.notifyListeners(SWT.Selection, new Event());
				return;
			}
		}

	}

	protected Menu popupMenuForTitle(ICard card, String title) {
		selectItem(card, title);
		final Menu menu = new Menu(shell);
		cardConfig.popupMenuService.contributeTo("popupmenuid", new Event(), menu, card);
		return menu;
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
		Assert.fail("Cannot find: " + title);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		httpClient = IHttpClient.Utils.builder().withCredentials(HttpClientConstants.userName, HttpClientConstants.password);
		repository = new RepositoryFacard(httpClient, "1.json");
		cardConfig = ICollectionConfigurationFactory.Utils.softwareFmConfigurator().configure(display, new CardConfig(ICardFactory.Utils.cardFactory(), ICardDataStore.Utils.repositoryCardDataStore(shell, repository)));
		IMasterDetailSocial masterDetailSocial = new MasterDetailSocial(shell, SWT.NULL);
		explorer = IExplorer.Utils.explorer(masterDetailSocial, cardConfig, CollectionConstants.rootUrl, IPlayListGetter.Utils.noPlayListGetter(), service);
		IBrowserConfigurator.Utils.configueWithUrlRssSnippetAndTweet(explorer);
	}

	@Override
	protected void tearDown() throws Exception {
		repository.shutdown();
		super.tearDown();
	}
}
