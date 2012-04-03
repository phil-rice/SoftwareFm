/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.internal;

public class CardCollectionsDataStoreWithNoFollowOnTest extends AbstractCardCollectionsDataStoreTest {
	public void testMainFutureIsDoneWhenInitialQueryReturnsWhenNoExtraDataNeeded() {
		assertFalse(status.mainTransaction.isDone());
		kickAndDispatch(status.initialTransaction);
		assertTrue(status.mainTransaction.isDone());
	}

	@Override
	protected String findFollowOnUrlFragmentForTest(String key, Object value) {
		return null;
	}

	@Override
	public void testCallbackNotCalledUntilAnyFollowUpQueriesFinished() {
		checkFuturesCount(0);
		kickAndDispatch(status.initialTransaction);
		checkFuturesCount(1);
	}

	private void checkFuturesCount(int expected) {
		assertEquals(expected, memory.finishedCount);
		assertEquals(0, memory.followedUpCount);

	}
}