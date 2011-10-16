package org.softwareFm.card.internal;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.KeyValue;

public class CardFactoryTest extends TestCase {

	@SuppressWarnings("unchecked")
	public void testAggregateLeavesStrings() {
		CardFactory cardFactory = new CardFactory(new CardConfig(), "tag", null);
		List<KeyValue> result = cardFactory.aggregate(CardDataStoreFixture.dataUrl1);
		assertEquals(Arrays.asList(new KeyValue("name1", "value1"),//
				new KeyValue("name2", "value2"),//
				new KeyValue("one", Arrays.asList(CardDataStoreFixture.data1a, CardDataStoreFixture.data1b)),//
				new KeyValue("two", Arrays.asList(CardDataStoreFixture.data2a, CardDataStoreFixture.data2b, CardDataStoreFixture.data2c))), result);
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
		CardFactory cardFactory = new CardFactory(null, null, null, "d", "c", "b");
		assertEquals(expected, cardFactory.comparator.compare(new KeyValue(left, null), new KeyValue(right, null)));
		assertEquals(-expected, cardFactory.comparator.compare(new KeyValue(right, null), new KeyValue(left, null)));
	}
}
