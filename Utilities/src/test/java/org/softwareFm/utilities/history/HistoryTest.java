/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.utilities.history;

import java.util.Arrays;

import junit.framework.TestCase;

import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.functions.IFunction1;

public class HistoryTest extends TestCase {

	private IHistory<Object> history;

	public void testHistoryProvidesPrev() {
		push(1, 2, 3, 4, 5);
		prev(4, 3, 2, 1);
	}

	public void testPrevGivesThePreviousValue() {
		push(1, 2, 3, 4, 5);
		prev(4, 3, 2, 1);
	}

	public void testLastIsTheLastItem(){
		push(1, 2, 3, 4, 5);
		assertEquals(5, history.last());
		prev(4);
		assertEquals(4, history.last());
		prev(3);
		assertEquals(3, history.last());
		prev(2,1,1);
		assertEquals(1, history.last());
		next(2,3,4,5,5,5);
		assertEquals(5, history.last());
		
	}
	
	public void testCanUseNextAfterPrev() {
		push(1, 2, 3, 4, 5);
		prev(4, 3, 2, 1);
		next(2, 3, 4, 5);
		next(5);
		prev(4, 3);
		next(4, 5, 5);

	}

	public void testPrevGivesTheFirstValueWhenRunsOutOfValues() {
		push(1, 2, 3, 4, 5);
		prev(4, 3, 2, 1, 1, 1, 1);
	}

	public void testNextAtEndHasNoEffectOnPush() {
		push(1, 2, 3, 4, 5);
		next(5);
		push(6, 7, 8);
		prev(7, 6, 5, 4, 3, 2, 1);
	}

	public void testPushKillsAllNextValues() {
		push(1, 2, 3, 4, 5);
		prev(4, 3);
		push(6, 7, 8);
		prev(7, 6, 3, 2, 1);
		next(2, 3, 6, 7);
	}

	public void testHasNextIsFalseIfHaveJustPushed() {
		push(1, 2, 3, 4, 5);
		hasNext(false);

		prev(4, 3, 2);
		push(6);
		hasNext(false);
	}

	public void testHasNextIsTrueIfHavePrevAndThereIsSomethingThere() {
		push(1, 2, 3, 4, 5);
		hasNext(false);

		prev(4);
		hasNext(true);

		prev(3, 2, 1);
		hasNext(true);

		prev(1, 1);
		hasNext(true);
	}

	public void testHasPrevIsTrueIfHaveJustPushed() {
		hasPrev(false);
		push(1);
		hasPrev(false);
		push(2);
		hasPrev(true);
		push(3, 4, 5);
		hasPrev(true);
		prev(4, 3, 2);
		hasPrev(true);
		prev(1);
		hasPrev(false);
	}

	private void hasPrev(boolean b) {
		assertEquals(b, history.hasPrevious());
	}

	public void testHasPrevIsTrueIfHaveNextedAndThereIsSomethingThere() {
		push(1, 2, 3, 4, 5);
		hasNext(false);

		prev(4);
		hasNext(true);

		prev(3, 2, 1);
		hasNext(true);

		prev(1, 1);
		hasNext(true);
	}

	public void testListenersFiredWithNextPrevAndPush() {
		HistoryListenerMock<Object> listener = new HistoryListenerMock<Object>();
		history.addHistoryListener(listener);
		push(1, 2, 3, 4, 5);
		prev(4, 3, 2, 1, 1, 1);
		next(2, 3, 4, 5, 5, 5);
		assertEquals(Arrays.asList(1, 2, 3, 4, 5, 4, 3, 2, 1, 1, 1, 2, 3, 4, 5, 5, 5), Lists.map(listener.values, new IFunction1<Object, Integer>() {
			@Override
			public Integer apply(Object from) throws Exception {
				return Integer.parseInt(from.toString());
			}
		}));
	}

	private void hasNext(boolean b) {
		assertEquals(b, history.hasNext());
	}

	private void prev(Object... expected) {
		for (Object string : expected)
			assertEquals(string, history.previous());
	}

	private void next(Object... expected) {
		for (Object string : expected)
			assertEquals(string, history.next());
	}

	public void testHistoryWithNextAfterPrev() {
		push("1", "2", "2", "2", "3", "4", "4", "5");

		assertEquals("4", history.previous());
		assertEquals("3", history.previous());
		assertEquals("2", history.previous());
		assertEquals("1", history.previous());
		assertEquals("2", history.next());
		assertEquals("3", history.next());
		assertEquals("4", history.next());
		assertEquals("5", history.next());
		assertEquals("5", history.next());
	}

	public void testHistoryIgnoresDuplicates() {
		push("1", "2", "2", "2", "3", "4", "4", "5");

		assertEquals("4", history.previous());
		assertEquals("3", history.previous());
		assertEquals("2", history.previous());
		assertEquals("1", history.previous());
		assertEquals("2", history.next());
		assertEquals("3", history.next());
		assertEquals("4", history.next());
		assertEquals("5", history.next());
		assertEquals("5", history.next());
	}

	
	
	private void push(Object... items) {
		for (Object item : items)
			history.push(item);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		history = new History<Object>();
	}
}