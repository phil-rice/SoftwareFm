/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.internal;

import java.util.Map;

import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.swt.card.CardMock;
import org.softwareFm.swt.card.LineItem;

public class CardCollectionsDataStoreWithFollowOnTest extends AbstractCardCollectionsDataStoreTest {

	public void testMainFutureIsNotDoneWhenInitialQueryReturns() {
		assertFalse(status.mainTransaction.isDone());
		kickAndDispatch(status.initialTransaction);
		assertFalse(status.mainTransaction.isDone());
	}

	public void testFollowOnQueriesAreSentWhenInitialQueryReturns() {
		assertEquals(0, status.keyValueTransactions.size());
		kickAndDispatch(status.initialTransaction);
		assertEquals(5, status.keyValueTransactions.size()); // 1a,2a,1b,2b,2c
	}

	public void testNotFinishedUntilAllFollowOnQueriesHaveFinished() {
		assertEquals(0, status.keyValueTransactions.size());
		kickAndDispatch(status.initialTransaction);
		assertEquals(5, status.keyValueTransactions.size());
		for (int i = 0; i < status.keyValueTransactions.size(); i++)
			assertFalse(status.keyValueTransactions.get(i).isDone());
		for (int i = 0; i < status.keyValueTransactions.size(); i++) {
			ITransaction<Object> t = status.keyValueTransactions.get(i);
			assertFalse("i: " + i, t.isDone());
			kickAndDispatch(t);
			assertTrue(t.isDone());
			assertEquals(i == 4, status.mainTransaction.isDone());
		}
	}

	public void testEachFollowOnQueryCausesCardToBeMutated()  {
		kickAndDispatch(status.initialTransaction);
		CardMock card = (CardMock) cardHolder.getCard();
		for (int i = 0; i < status.keyValueTransactions.size(); i++) {
			ITransaction<Object> t = status.keyValueTransactions.get(i);
			int start = card.keys.size();
			kickAndDispatch(t);
			LineItem lineItem = (LineItem) t.get(CommonConstants.testTimeOutMs);
			assertEquals(start + 1, card.keys.size());
			assertEquals(lineItem.key, card.keys.get(i));
			// should really test value
		}
	}

	@Override
	public void testCallbackNotCalledUntilAnyFollowUpQueriesFinished() {
		assertEquals(0, memory.initialCardCount);
		kickAndDispatch(status.initialTransaction);
		for (int i = 0; i < status.keyValueTransactions.size(); i++) {
			ITransaction<Object> t = status.keyValueTransactions.get(i);
			kickAndDispatch(t);
			assertEquals("I: " + i, i != status.keyValueTransactions.size() - 1, memory.finishedCount == 0);
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