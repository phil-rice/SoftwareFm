package org.softwareFm.card.internal;

import org.softwareFm.card.api.KeyValue;

public class CardCollectionsDataStoreWithNoFollowOnTest extends AbstractCardCollectionsDataStoreTest {
	public void testMainFutureIsDoneWhenInitialQueryReturnsWhenNoExtraDataNeeded() {
		assertFalse(status.mainFuture.isDone());
		kickAndDispatch(status.initialFuture);
		assertTrue(status.mainFuture.isDone());
	}

	@Override
	protected String findFollowOnUrlFragment(KeyValue keyValue) {
		return null;
	}

	@Override
	public void testCallbackNotCalledUntilAnyFollowUpQueriesFinished() {
		checkFuturesCount(0);
		kickAndDispatch(status.initialFuture);
		checkFuturesCount(1);
	}

	private void checkFuturesCount(int expected) {
		assertEquals(expected, memory.finishedCount);
		assertEquals(0, memory.followedUpCount);

	}
}
