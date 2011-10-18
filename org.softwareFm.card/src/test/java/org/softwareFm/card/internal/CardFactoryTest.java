package org.softwareFm.card.internal;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.api.KeyValue;

public class CardFactoryTest extends TestCase {

	@SuppressWarnings("unchecked")
	public void testAggregateLeavesStrings() {
		CardFactory cardFactory = new CardFactory(null, "tag");
		List<KeyValue> result = cardFactory.aggregate(CardDataStoreFixture.dataUrl1);
		assertEquals(Arrays.asList(new KeyValue("name1", "value1"),//
				new KeyValue("name2", "value2"),//
				new KeyValue("one", Arrays.asList(new KeyValue("data1a", CardDataStoreFixture.data1a), new KeyValue("data1b", CardDataStoreFixture.data1b))),//
				new KeyValue("two", Arrays.asList(new KeyValue("data2a", CardDataStoreFixture.data2a), new KeyValue("data2b", CardDataStoreFixture.data2b), new KeyValue("data2c", CardDataStoreFixture.data2c)))), result);
	}

}
