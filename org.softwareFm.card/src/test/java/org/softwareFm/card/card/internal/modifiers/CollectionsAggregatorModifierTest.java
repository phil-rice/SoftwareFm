package org.softwareFm.card.card.internal.modifiers;

import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.modifiers.internal.CollectionsAggregatorModifier;
import org.softwareFm.utilities.maps.Maps;

public class CollectionsAggregatorModifierTest extends TestCase {

	private final static Map<String, Object> item1_1 = Maps.makeImmutableMap("item1", 1); // for example this is a plain 'card'
	private final static Map<String, Object> item1_2 = Maps.makeImmutableMap("item1", 2);

	private final static Map<String, Object> item1_t1 = Maps.makeImmutableMap("item1", 1, "tag", "t1"); // for example this is a group
	private final static Map<String, Object> item1_t2 = Maps.makeImmutableMap("item1", 2, "tag", "t2");
	private final static Map<String, Object> item2_t1 = Maps.makeImmutableMap("item2", 1, "tag", "t1");
	private final static Map<String, Object> item2_t2 = Maps.makeImmutableMap("item2", 2, "tag", "t2");

	private final static Map<String, Object> comp1 = Maps.makeImmutableMap("c1_1", item1_1, "c1_2", item1_2, "tag", "coll1"); // for example this could be a 'collection' of cards. Like a collection of rss feeds (the feeds are probably not tagged)
	private final static Map<String, Object> comp2 = Maps.makeImmutableMap("c2_1", item1_1, "c2_2", item1_2, "tag", "coll2"); // And this is a second collection

	private final static Map<String, Object> compWithOneTaggedChildren = Maps.makeImmutableMap("ct1", item1_t1, "tag", "irrelevant"); // for example this could be a 'collection' of tagged cards. Like the artifacts collection
	private final static Map<String, Object> compWithTwoDifferentlyTaggedChildren = Maps.makeImmutableMap("ct1", item1_t1, "ct2", item1_t2, "tag", "irrelevant"); // for example this could be a 'collection' of tagged cards. Like the artifacts collection
	private final static Map<String, Object> compWithTwoSameTaggedChildren = Maps.makeImmutableMap("ct1", item1_t1, "ct2", item2_t1, "tag", "irrelevant"); // for example this could be a 'collection' of tagged cards. Like the artifacts collection
	private final static Map<String, Object> compWithFourTaggedChildren = Maps.makeImmutableMap("ct11", item1_t1, "ct12", item1_t2, "ct21", item2_t1, "ct22", item2_t2, "tag", "irrelevant"); // for example this could be a 'collection' of tagged cards. Like the artifacts collection


	private final static CollectionsAggregatorModifier modifier = new CollectionsAggregatorModifier("tag");
	private CardConfig cardConfig;

	@Test
	public void testItemsWithNoTaggedChildMapsAreLeftAlone() {
		check(item1_1, item1_1);
		check(item1_2, item1_2);
		check(comp1, comp1);
		check(comp2, comp2);
	}

	public void testIfInputHasTagItIsIgnored() {
		check(comp1, comp1);
		check(comp2, comp2);
		check(item1_t1, item1_t1);
		check(item1_t2, item1_t2);
	}

	//TODO Come back and sort this out...
	public void testTransformsKeyValueWhenValueHasTagIntoTagAndListOfValuesThatShareTag() {
		check(compWithOneTaggedChildren, Maps.stringObjectMap(//
				"ct1", item1_t1, //
				"tag", "irrelevant"));
		check(compWithTwoDifferentlyTaggedChildren, Maps.stringObjectMap(//
				"ct1", item1_t1, //
				"ct2", item1_t2, //
				"tag", "irrelevant"));
		check(compWithTwoSameTaggedChildren, Maps.stringObjectMap(//
				"ct1", item1_t1, "ct2", item2_t1, //
				"tag", "irrelevant"));
		check(compWithFourTaggedChildren, Maps.stringObjectMap(//
				"ct11", item1_t1, "ct21", item2_t1, //
				"ct12", item1_t2, "ct22", item2_t2, //
				"tag", "irrelevant"));
	}

	private void check(Map<String, Object> input, Map<String, Object> expected) {
		Map<String, Object> actual = modifier.modify(cardConfig, "someurl", input);
		assertEquals(expected, actual);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardConfig = new CardConfig(ICardFactory.Utils.cardFactory(), CardDataStoreFixture.rawCardStore());
	}

}
