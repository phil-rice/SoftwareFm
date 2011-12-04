package org.softwareFm.collections.explorer.internal;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.SWT;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.CardAndCollectionDataStoreAdapter;
import org.softwareFm.card.dataStore.ICardDataStore;
import org.softwareFm.collections.ICollectionConfigurationFactory;
import org.softwareFm.collections.constants.CollectionConstants;
import org.softwareFm.collections.explorer.IExplorer;
import org.softwareFm.collections.explorer.IMasterDetailSocial;
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
	protected final long delay = 10000;

	public static interface CardHolderAndCardCallback {
		void process(ICardHolder cardHolder, ICard card);
	}


	

	protected void displayCard(String url, final CardHolderAndCardCallback cardHolderAndCardCallback) {
		try {
			final AtomicInteger count = new AtomicInteger();
			final CountDownLatch latch = new CountDownLatch(1);
			explorer.displayCard(CollectionConstants.rootUrl + url, new CardAndCollectionDataStoreAdapter() {
				@Override
				public void finished(ICardHolder cardHolder, String url, ICard card) {
					assertEquals(1, count.incrementAndGet());
					count.incrementAndGet();
					dispatchUntilQueueEmpty();
					cardHolderAndCardCallback.process(cardHolder, card);
					latch.countDown();
				}
			});
			while (latch.getCount()>0) {
				Thread.sleep(10);
				dispatchUntilQueueEmpty();
			}
		} catch (InterruptedException e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		httpClient = IHttpClient.Utils.builder().withCredentials(HttpClientConstants.userName, HttpClientConstants.password);
		repository = new RepositoryFacard(httpClient, "1.json");
		cardConfig = ICollectionConfigurationFactory.Utils.softwareFmConfigurator().configure(display, new CardConfig(ICardFactory.Utils.cardFactory(), ICardDataStore.Utils.repositoryCardDataStore(shell, repository)));
		IMasterDetailSocial masterDetailSocial = new MasterDetailSocial(shell, SWT.NULL);
		explorer = IExplorer.Utils.explorer(masterDetailSocial, cardConfig, CollectionConstants.rootUrl, IPlayListGetter.Utils.noPlayListGetter(), service);
	}

	@Override
	protected void tearDown() throws Exception {
		repository.shutdown();
		super.tearDown();
	}
}
