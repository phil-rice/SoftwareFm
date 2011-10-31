package org.softwareFm.card.internal;

import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import org.softwareFm.card.api.CardAndCollectionDataStoreVisitorMock;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardDataStoreAsyncMock;
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.api.CardFactoryMock;
import org.softwareFm.card.api.CardMock;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.display.swt.SwtIntegrationTest;

public abstract class AbstractCardCollectionsDataStoreTest extends SwtIntegrationTest {

	protected CardDataStoreAsyncMock rawAsyncCardStore;
	protected CardConfig cardConfig;
	protected CardHolder cardHolder;
	protected CardCollectionsDataStore cardCollectionsDataStore;
	protected CardAndCollectionsStatus status;
	protected CardAndCollectionsStatus followUpQueryStatus;
	private CardFactoryMock mockCardFactory;
	protected CardAndCollectionDataStoreVisitorMock memory;

	public void testNothingHappenBeforeInitialQueryReturns() throws InterruptedException, ExecutionException {
		assertEquals(1, memory.initialUrlCount);
		dispatchUntilQueueEmpty();
		assertFalse(status.mainFuture.isDone());
		assertEquals(0, status.keyValueFutures.size());
		assertEquals(1, status.count.get());
		assertEquals(0, mockCardFactory.count);
		assertEquals(0, memory.initialCardCount);
	}

	public void testCardCreatedAfterInitialQueryReturns() {
		kickAndDispatch(status.initialFuture);
		assertEquals(1, mockCardFactory.count);
		CardMock card = (CardMock) cardHolder.getCard();
		assertEquals(CardDataStoreFixture.url, card.url());
		assertEquals(CardDataStoreFixture.dataUrl1, card.map);
	}

	public void testFollowOnQueriesDontSendKeyValuesToCardBeforeAnyFollowOnQueries() {
		kickAndDispatch(status.initialFuture);
		CardMock card = (CardMock) cardHolder.getCard();
		assertEquals(0, card.keys.size());
	}

	abstract public void testCallbackNotCalledUntilAnyFollowUpQueriesFinished();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rawAsyncCardStore = CardDataStoreFixture.rawAsyncCardStore();
		mockCardFactory = ICardFactory.Utils.mockCardFactory();
		cardConfig = new CardConfig(mockCardFactory, rawAsyncCardStore);
		cardHolder = new CardHolder(shell, "loading", "title", cardConfig, CardDataStoreFixture.url, null);
		cardCollectionsDataStore = new CardCollectionsDataStore() {
			@Override
			protected String findFollowOnUrlFragment(Entry<String, Object> entry) {
				return AbstractCardCollectionsDataStoreTest.this.findFollowOnUrlFragment(entry);
			}
		};
		memory = new CardAndCollectionDataStoreVisitorMock();
		status = cardCollectionsDataStore.processDataFor(cardHolder, cardConfig, CardDataStoreFixture.url, memory);
	}

	abstract protected String findFollowOnUrlFragment(Entry<String, Object> entry);

}
