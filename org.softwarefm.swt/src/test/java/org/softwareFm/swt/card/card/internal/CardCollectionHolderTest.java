/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.card.internal;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.dataStore.CardDataStoreAsyncMock;
import org.softwareFm.swt.card.dataStore.CardDataStoreFixture;
import org.softwareFm.swt.card.internal.CardCollectionHolder;
import org.softwareFm.swt.card.internal.CardCollectionHolder.CardCollectionHolderComposite;
import org.softwareFm.swt.card.internal.CardHolder.CardHolderComposite;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.dataStore.IMutableCardDataStore;
import org.softwareFm.swt.details.IDetailsFactoryCallback;
import org.softwareFm.swt.swt.SwtTest;

public class CardCollectionHolderTest extends SwtTest {

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
		cardCollectionHolder.setKeyValue("some", "url", Maps.stringObjectLinkedMap(CardDataStoreFixture.dataIndexedByUrlFragment), callback);
		checkPaintCausesRequest(0, "/some/url/1a", CardDataStoreFixture.data1ap);
		checkPaintCausesRequest(1, "/some/url/1b", CardDataStoreFixture.data1bp);
		for (int i = 3; i < 6; i++)
			checkChildhasNullCard(i);
		checkPaintCausesRequest(2, "/some/url/2a", CardDataStoreFixture.data2ap);
		checkPaintCausesRequest(3, "/some/url/2b", CardDataStoreFixture.data2bp);
		checkPaintCausesRequest(4, "/some/url/2c", CardDataStoreFixture.data2cp);
		// last item is some...which will throw an exception is we try and paint it
		assertEquals(0, callback.cardUrls.size());
	}

	public void testAfterPaintListenerFiresThatTableSelectWillFireCardSelectedListeners() {
		cardCollectionHolder.setKeyValue("some", "url", Maps.stringObjectLinkedMap(CardDataStoreFixture.dataIndexedByUrlFragment), callback);
		CardHolderComposite control = (CardHolderComposite) composite.getChildren()[0];
		control.notifyListeners(SWT.Paint, new Event());
		mockDataStore.kickAllFutures();
		dispatchUntilQueueEmpty();

		ICard card = control.card;
		assertEquals(0, callback.cardUrls.size());

		Table table = card.getTable();
		table.select(0);
		table.notifyListeners(SWT.Selection, new Event());
		assertEquals("/some/url/1a", Lists.getOnly(callback.cardUrls));

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

	@SuppressWarnings("unused")
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