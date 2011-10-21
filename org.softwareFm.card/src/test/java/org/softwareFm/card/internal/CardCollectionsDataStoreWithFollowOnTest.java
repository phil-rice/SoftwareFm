package org.softwareFm.card.internal;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.softwareFm.card.api.CardMock;
import org.softwareFm.card.api.KeyValue;

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
			Future<KeyValue> f = status.keyValueFutures.get(i);
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
			Future<KeyValue> f = status.keyValueFutures.get(i);
			int start = card.keyValues.size();
			kickAndDispatch(f);
			KeyValue keyValue = f.get(1, TimeUnit.SECONDS);
			assertEquals(start + 1, card.keyValues.size());
			assertEquals(keyValue, card.keyValues.get(i));
			// should really test value
		}
	}

	@Override
	public void testCallbackNotCalledUntilAnyFollowUpQueriesFinished() {
		assertEquals(0, memory.getResult().size());
		kickAndDispatch(status.initialFuture);
		for (int i = 0; i < status.keyValueFutures.size(); i++) {
			Future<KeyValue> f = status.keyValueFutures.get(i);
			assertEquals(true, memory.getResult().size() == 0);
			kickAndDispatch(f);
			assertEquals(i != 4, memory.getResult().size() == 0);
		}
		CardMock card = (CardMock) cardHolder.getCard();
		assertEquals(card, memory.getOnlyResult());
	}

	@Override
	protected String findFollowOnUrlFragment(KeyValue keyValue) {
		if (keyValue.value instanceof Map<?, ?>) {
			Map<?, ?> map = (Map<?, ?>) keyValue.value;
			return (String) map.get("value");
		} else
			return null;
	}
}
