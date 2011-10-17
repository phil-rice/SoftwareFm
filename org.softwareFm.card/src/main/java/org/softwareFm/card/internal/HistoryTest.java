package org.softwareFm.card.internal;

import junit.framework.TestCase;

public class HistoryTest extends TestCase {

	public void testHistoryProvidesPrev() {
		History<String> history = new History<String>();
		push(history, "1", "2", "3", "4", "5");
		assertEquals("4", history.prev());
		assertEquals("3", history.prev());
		assertEquals("2", history.prev());
		assertEquals("1", history.prev());
		assertEquals("1", history.prev());
		assertEquals("1", history.prev());
	}
	public void testHistoryProvidesNext() {
		History<String> history = new History<String>();
		push(history, "1", "2", "3", "4", "5");
		assertEquals("5", history.next());
		assertEquals("5", history.next());
		
		assertEquals("4", history.prev());
		assertEquals("3", history.prev());
		assertEquals("2", history.prev());
		assertEquals("1", history.prev());
		assertEquals("1", history.prev());
		assertEquals("1", history.prev());

		assertEquals("2", history.next());
		assertEquals("3", history.next());
		assertEquals("4", history.next());
		assertEquals("5", history.next());
		assertEquals("5", history.next());
	}
	
	
	public void testHistoryIgnoresDuplicates(){
		History<String> history = new History<String>();
		push(history, "1", "2","2", "2", "3", "4", "4", "5");
		
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

	private <T> void push(History<T> history, T... items) {
		for (T item : items)
			history.push(item);

	}

}
