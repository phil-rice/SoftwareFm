package org.softwareFm.card.card.internal;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.internal.CardCollectionHolder.CardCollectionHolderComposite;
import org.softwareFm.card.card.internal.CardHolder.CardHolderComposite;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.CardDataStoreAsyncMock;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.dataStore.IMutableCardDataStore;
import org.softwareFm.card.details.IDetailsFactoryCallback;
import org.softwareFm.display.swt.SwtIntegrationTest;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;

public class CardCollectionHolderTest extends SwtIntegrationTest {

	private CardCollectionHolder cardCollectionHolder;
	private CardCollectionHolder.CardCollectionHolderComposite composite;
	private DetailsFactoryCallback callback;
	private CardConfig asyncCardConfig;
	private CardDataStoreAsyncMock mockDataStore;

	public void testConstructor() {
		assertEquals(1, composite.getChildren().length); // just the label
	}

	public void testSetKeyValueIfValueIsAMapMakesACardHolderForEachMapInValue() {
		cardCollectionHolder.setKeyValue("some", "key", Maps.stringObjectLinkedMap(CardDataStoreFixture.dataIndexedByUrlFragment), callback);
		assertEquals(1, callback.count.get());
		assertEquals(6, composite.getChildren().length);
		dispatchUntilQueueEmpty();// not needed, but just being carefull
		for (int i = 0; i < 6; i++)
			checkChildhasNullCard(i);
		assertEquals(0, callback.cardUrls.size());
	}

	public void testCardHolderPaintListenersCauseDataRequestForCard() {
		cardCollectionHolder.setKeyValue("some/url", "key", Maps.stringObjectLinkedMap(CardDataStoreFixture.dataIndexedByUrlFragment), callback);
		checkPaintCausesRequest(0, "some/url/1a", CardDataStoreFixture.data1ap);
		checkPaintCausesRequest(1, "some/url/1b", CardDataStoreFixture.data1bp);
		for (int i = 3; i < 6; i++)
			checkChildhasNullCard(i);
		checkPaintCausesRequest(2, "some/url/2a", CardDataStoreFixture.data2ap);
		checkPaintCausesRequest(3, "some/url/2b", CardDataStoreFixture.data2bp);
		checkPaintCausesRequest(4, "some/url/2c", CardDataStoreFixture.data2cp);
		// last item is some...which will throw an exception is we try and paint it
		assertEquals(0, callback.cardUrls.size());
	}

	public void testAfterPaintListenerFiresThatTableSelectWillFireCardSelectedListeners() {
		cardCollectionHolder.setKeyValue("some/url", "key", Maps.stringObjectLinkedMap(CardDataStoreFixture.dataIndexedByUrlFragment), callback);
		CardHolderComposite control = (CardHolderComposite) composite.getChildren()[0];
		control.notifyListeners(SWT.Paint, new Event());
		mockDataStore.kickAllFutures();
		dispatchUntilQueueEmpty();

		ICard card = control.card;
		assertEquals(0, callback.cardUrls.size());
		
		Table table = card.getTable();
		table.select(0);
		table.notifyListeners(SWT.Selection, new Event());
		assertEquals("some/url/1a", Lists.getOnly(callback.cardUrls));

	}

	private void checkPaintCausesRequest(int childIndex, String url, Object data) {
		CardHolderComposite control = (CardHolderComposite) composite.getChildren()[childIndex];
		assertNull(control.card);
		control.notifyListeners(SWT.Paint, new Event());
		mockDataStore.kickAllFutures();
		dispatchUntilQueueEmpty();

		ICard card = control.card;
		assertEquals(url, card.url());
		assertEquals(data, card.data());

	}

	private void checkChildhasNullCard(int childIndex) {
		CardHolderComposite control = (CardHolderComposite) composite.getChildren()[childIndex];
		ICard card = control.card;
		assertNull(card);
		mockDataStore.kickAllFutures();
		dispatchUntilQueueEmpty();
		assertNull(control.card);

	}

	public void testSetKeyValueDisplaysNothingIfValueIsntMap() {
		cardCollectionHolder.setKeyValue("someRoot", "key", "value", callback);
		assertEquals(1, callback.count.get());
		assertEquals(0, composite.getChildren().length);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		asyncCardConfig = CardDataStoreFixture.asyncCardConfig(shell.getDisplay());
		mockDataStore = (CardDataStoreAsyncMock) asyncCardConfig.cardDataStore;
		cardCollectionHolder = new CardCollectionHolder(shell, asyncCardConfig);
		composite = (CardCollectionHolderComposite) cardCollectionHolder.getComposite();
		callback = new DetailsFactoryCallback(composite);
		new Label(composite, SWT.NULL);// here to be removed!
	}

	private static final class DetailsFactoryCallback implements IDetailsFactoryCallback {
		public final AtomicInteger count = new AtomicInteger();
		private final Control expectedControl;
		private final List<String> cardUrls = Lists.newList();

		DetailsFactoryCallback(Control expectedControl) {
			this.expectedControl = expectedControl;

		}

		@Override
		public void cardSelected(String cardUrl) {
			cardUrls.add(cardUrl);
		}

		@Override
		public void gotData(Control control) {
			count.incrementAndGet();
			assertSame(expectedControl, control);
		}

		@Override
		public void afterEdit(String url) {
			fail();
		}

		@Override
		public void updateDataStore(IMutableCardDataStore store, String url, String key, Object value) {
			fail();
		}
	}
}