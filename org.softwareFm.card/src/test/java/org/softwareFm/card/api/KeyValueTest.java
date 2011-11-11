package org.softwareFm.card.api;

import java.util.Comparator;

import junit.framework.TestCase;

import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.tests.Tests;

public class KeyValueTest extends TestCase {

	public void testConstructor() {
		KeyValue keyValue = new KeyValue("key", "value");
		assertEquals("key", keyValue.key);
		assertEquals("value", keyValue.value);
	}

	public void testKeyFunctio() throws Exception {
		assertEquals("key", KeyValue.Utils.keyFn().apply(new KeyValue("key", "value")));

	}

	public void testEquality() {
		Tests.checkEqualityAndHashcode(new IFunction1<String, KeyValue>() {
			@Override
			public KeyValue apply(String from) throws Exception {
				return new KeyValue(from + "_key", from + "_value");
			}
		});
		assertFalse(new KeyValue("k", "v").equals(new KeyValue("k", "v2")));
		assertFalse(new KeyValue("k", "v").equals(new KeyValue("k2", "v")));
	}

	public void testForItemsInSortOrder() {
		checkComparator("d", "c", -1);
		checkComparator("d", "b", -2);
		checkComparator("c", "b", -1);
		checkComparator("b", "b", -0);
	}

	public void testAllItemsInSortOrderAreLessThanAnyOther() {
		checkComparator("b", "a", -1);
		checkComparator("c", "a", -1);
		checkComparator("d", "a", -1);

		checkComparator("b", "e", -1);
		checkComparator("c", "e", -1);
		checkComparator("d", "e", -1);
	}

	public void testSortedAlphabeticallyIfNotInSortOrder() {
		checkComparator("a", "a", 0);
		checkComparator("a", "f", -5);
		checkComparator("e", "f", -1);
		checkComparator("a", "e", -4);
	}

	private void checkComparator(String left, String right, int expected) {
		Comparator<KeyValue> comparator = KeyValue.Utils.orderedKeyComparator("d", "c", "b");
		assertEquals(expected, comparator.compare(new KeyValue(left, null), new KeyValue(right, null)));
		assertEquals(-expected, comparator.compare(new KeyValue(right, null), new KeyValue(left, null)));
	}

}
