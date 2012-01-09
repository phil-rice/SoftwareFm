/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card.internal;

import org.softwareFm.card.card.CardFactoryMock;
import org.softwareFm.card.card.CardMock;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.CardAndCollectionDataStoreVisitorMonitored;
import org.softwareFm.card.dataStore.CardAndCollectionsStatus;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.dataStore.IFollowOnFragment;
import org.softwareFm.display.swt.SwtTest;

public abstract class AbstractCardCollectionsDataStoreTest extends SwtTest {

	protected CardConfig cardConfig;
	protected ICardHolder cardHolder;
	protected CardAndCollectionsStatus status;
	protected CardAndCollectionsStatus followUpQueryStatus;
	private CardFactoryMock mockCardFactory;
	protected CardAndCollectionDataStoreVisitorMonitored memory;

	public void testNothingHappenBeforeInitialQueryReturns() {
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
		assertEquals("/" + CardDataStoreFixture.url, card.url());
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
		mockCardFactory = ICardFactory.Utils.mockCardFactory();
		cardConfig = CardDataStoreFixture.asyncCardConfig(shell.getDisplay()).withCardFactory(mockCardFactory).withFollowOn(new IFollowOnFragment() {
			@Override
			public String findFollowOnFragment(String key, Object value) {
				return findFollowOnUrlFragmentForTest(key, value);
			}
		});
		cardHolder = ICardHolder.Utils.cardHolderWithLayout(shell, cardConfig, CardDataStoreFixture.urlAsList, null);
		memory = new CardAndCollectionDataStoreVisitorMonitored();
		status = cardConfig.cardCollectionsDataStore.processDataFor(cardHolder, cardConfig, "/" + CardDataStoreFixture.url, memory);
	}

	abstract protected String findFollowOnUrlFragmentForTest(String key, Object value);

}