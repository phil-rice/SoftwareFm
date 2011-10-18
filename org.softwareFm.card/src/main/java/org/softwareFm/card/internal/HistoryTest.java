package org.softwareFm.card.internal;

import junit.framework.TestCase;

public class HistoryTest extends TestCase {

	private History<Object> history;

	public void testHistoryProvidesPrev() {
		push(1, 2, 3, 4, 5);
		prev(4, 3, 2, 1);
	}

	public void testPrevGivesThePreviousValue() {
		push(1, 2, 3, 4, 5);
		prev(4, 3, 2, 1);
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

	private void prev(Object... expected) {
		for (Object string : expected)
			assertEquals(string, history.prev());
	}

	private void next(Object... expected) {
		for (Object string : expected)
			assertEquals(string, history.next());
	}

	public void testHistoryWithNextAfterPrev() {
		push("1", "2", "2", "2", "3", "4", "4", "5");

		assertEquals("4", history.prev());
		assertEquals("3", history.prev());
		assertEquals("2", history.prev());
		assertEquals("1", history.prev());
		assertEquals("2", history.next());
		assertEquals("3", history.next());
		assertEquals("4", history.next());
		assertEquals("5", history.next());
		assertEquals("5", history.next());
	}

	public void testHistoryIgnoresDuplicates() {
		push("1", "2", "2", "2", "3", "4", "4", "5");

		assertEquals("4", history.prev());
		assertEquals("3", history.prev());
		assertEquals("2", history.prev());
		assertEquals("1", history.prev());
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
