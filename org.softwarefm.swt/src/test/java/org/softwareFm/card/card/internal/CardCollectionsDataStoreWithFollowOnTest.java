/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card.internal;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.softwareFm.card.card.CardMock;
import org.softwareFm.swt.card.LineItem;

public class CardCollectionsDataStoreWithFollowOnTest extends AbstractCardCollectionsDataStoreTest {

	public void testMainFutureIsNotDoneWhenInitialQueryReturns() {
		assertFalse(status.mainFuture.isDone());
		kickAndDispatch(status.initialFuture);
		assertFalse(status.mainFuture.isDone());
	}

	public void testFollowOnQueriesAreSentWhenInitialQueryReturns() {
		assertEquals(0, status.keyValueFutures.size());
		kickAndDispatch(status.initialFuture);
		assertEquals(5, status.keyValueFutures.size()); // 1a,2a,1b,2b,2c
	}

	public void testNotFinishedUntilAllFollowOnQueriesHaveFinished() {
		assertEquals(0, status.keyValueFutures.size());
		kickAndDispatch(status.initialFuture);
		assertEquals(5, status.keyValueFutures.size());
		for (int i = 0; i < status.keyValueFutures.size(); i++)
			assertFalse(status.keyValueFutures.get(i).isDone());
		for (int i = 0; i < status.keyValueFutures.size(); i++) {
			Future<?> f = status.keyValueFutures.get(i);
			assertFalse("i: " + i, f.isDone());
			kickAndDispatch(f);
			assertTrue(f.isDone());
			assertEquals(i == 4, status.mainFuture.isDone());
		}
	}

	public void testEachFollowOnQueryCausesCardToBeMutated() throws InterruptedException, ExecutionException, TimeoutException {
		kickAndDispatch(status.initialFuture);
		CardMock card = (CardMock) cardHolder.getCard();
		for (int i = 0; i < status.keyValueFutures.size(); i++) {
			Future<?> f = status.keyValueFutures.get(i);
			int start = card.keys.size();
			kickAndDispatch(f);
			LineItem lineItem = (LineItem) f.get(1, TimeUnit.SECONDS);
			assertEquals(start + 1, card.keys.size());
			assertEquals(lineItem.key, card.keys.get(i));
			// should really test value
		}
	}

	@Override
	public void testCallbackNotCalledUntilAnyFollowUpQueriesFinished() {
		assertEquals(0, memory.initialCardCount);
		kickAndDispatch(status.initialFuture);
		for (int i = 0; i < status.keyValueFutures.size(); i++) {
			Future<?> f = status.keyValueFutures.get(i);
			kickAndDispatch(f);
			assertEquals("I: " + i, i != status.keyValueFutures.size() - 1, memory.finishedCount == 0);
		}
	}

	@Override
	protected String findFollowOnUrlFragmentForTest(String key, Object value) {
		if (value instanceof Map<?, ?>) {
			Map<?, ?> map = (Map<?, ?>) value;
			return (String) map.get("value");
		} else
			return null;
	}
}