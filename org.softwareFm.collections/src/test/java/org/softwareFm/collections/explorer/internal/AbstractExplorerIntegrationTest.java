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
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.CardAndCollectionDataStoreAdapter;
import org.softwareFm.card.dataStore.ICardDataStore;
import org.softwareFm.collections.ICollectionConfigurationFactory;
import org.softwareFm.collections.explorer.ExplorerAdapter;
import org.softwareFm.collections.explorer.IExplorer;
import org.softwareFm.display.browser.IBrowserConfigurator;
import org.softwareFm.display.swt.SwtAndServiceTest;
import org.softwareFm.display.timeline.IPlayListGetter;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.constants.HttpClientConstants;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.repositoryFacard.impl.RepositoryFacard;
import org.softwareFm.utilities.maps.Maps;

/** These tests go out to software fm, so they are much more fragile */
abstract public class AbstractExplorerIntegrationTest extends SwtAndServiceTest {

	final static String artifactUrl = "/ant/ant/artifact/ant";
	static boolean addedArtifact = false;

	protected CardConfig cardConfig;
	protected RepositoryFacard repository;
	protected Explorer explorer;
	protected IHttpClient httpClient;
	protected final long delay = 200000;
	protected MasterDetailSocial masterDetailSocial;
	
	protected final String rootUrl = "/tests/"+ getClass().getSimpleName();

	public static interface CardHolderAndCardCallback {
		void process(ICardHolder cardHolder, ICard card) throws Exception;
	}

	protected void displayCard(final String url, final CardHolderAndCardCallback cardHolderAndCardCallback) {
		doSomethingAndWaitForCardDataStoreToFinish(new Runnable() {
			@Override
			public void run() {
				explorer.displayCard(rootUrl + url, new CardAndCollectionDataStoreAdapter());
			}
		}, cardHolderAndCardCallback);

	}

	protected void doSomethingAndWaitForCardDataStoreToFinish(Runnable something, final CardHolderAndCardCallback cardHolderAndCardCallback) {
		final CountDownLatch latch = new CountDownLatch(1);
		explorer.addExplorerListener(new ExplorerAdapter() {
			@Override
			public void finished(ICardHolder cardHolder, String url, ICard card) throws Exception {
				explorer.removeExplorerListener(this);
				cardHolderAndCardCallback.process(cardHolder, card);
				dispatchUntilQueueEmpty();
				latch.countDown();
			}
		});
		something.run();
		dispatchUntilTimeoutOrLatch(latch, delay);
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
		masterDetailSocial = new MasterDetailSocial(shell, SWT.NULL);
		explorer = (Explorer) IExplorer.Utils.explorer(masterDetailSocial, cardConfig, rootUrl, IPlayListGetter.Utils.noPlayListGetter(), service);
		IBrowserConfigurator.Utils.configueWithUrlRssSnippetAndTweet(explorer);
		if (!addedArtifact){
			httpClient.delete(rootUrl+artifactUrl).execute(IResponseCallback.Utils.noCallback()).get();
			repository.post(rootUrl+artifactUrl, Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.artifact), IResponseCallback.Utils.noCallback()).get();
			repository.post(rootUrl+artifactUrl+"/tutorial", Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.collection), IResponseCallback.Utils.noCallback()).get();
			repository.post(rootUrl+artifactUrl+"/tutorial/one", Maps.stringObjectMap(CardConstants.slingResourceType, "tutorial"), IResponseCallback.Utils.noCallback()).get();
			repository.post(rootUrl+artifactUrl+"/tutorial/two", Maps.stringObjectMap(CardConstants.slingResourceType, "tutorial"), IResponseCallback.Utils.noCallback()).get();
			addedArtifact = true;
		}
	}

	@Override
	protected void tearDown() throws Exception {
		repository.shutdown();
		super.tearDown();
	}
}
