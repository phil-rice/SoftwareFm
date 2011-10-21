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
		assertEquals(0, memory.getResult().size());
		kickAndDispatch(status.initialFuture);
		assertEquals(1, memory.getResult().size());
	}
}
