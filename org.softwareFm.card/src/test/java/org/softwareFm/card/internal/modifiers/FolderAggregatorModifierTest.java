package org.softwareFm.card.internal.modifiers;

import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.utilities.maps.Maps;

public class FolderAggregatorModifierTest extends TestCase {
	// for example tag is jcr:primarytype and the looked for value is nt:unstructured

	private final static Map<String, Object> item1_1 = Maps.makeImmutableMap("item1", 1);
	private final static Map<String, Object> item1_2 = Maps.makeImmutableMap("item1", 2);

	private final static Map<String, Object> item1_t1 = Maps.makeImmutableMap("item1", 1, "tag", "t1");
	private final static Map<String, Object> item2_t1 = Maps.makeImmutableMap("item2", 1, "tag", "t1");
	private final static Map<String, Object> item3_t1 = Maps.makeImmutableMap("item1", 2, "tag", "t1");
	private final static Map<String, Object> item1_t2 = Maps.makeImmutableMap("item2", 2, "tag", "t2");

	private final static Map<String, Object> noTags = Maps.makeImmutableMap("1_1", item1_1, "1_2", item1_2, "tag", "coll1");
	private final static Map<String, Object> tagsWithCorrectValues = Maps.makeImmutableMap("1_1", item1_t1, "2_1", item2_t1,  "3_1", item3_t1, "tag", "irrelevant");
	private final static Map<String, Object> tagsWithInCorrectValues = Maps.makeImmutableMap("1_1", item1_t2, "junk", 1);
	private final static Map<String, Object> tagsWithCorrectAndInCorrectValues = Maps.makeImmutableMap("1_1", item1_t1, "2_1", item2_t1, "3_1", item3_t1, "tag", "irrelevant", "1_2", item1_t2);

	private final FolderAggregatorModifier modifier = new FolderAggregatorModifier("tag", "t1","ignoreThisTag");
	private CardConfig cardConfig;

	@Test
	public void testPassesThroughItemsWithoutTags() {
		check(item1_1, item1_1);
		check(item1_2, item1_2);
	}

	public void testIgnoresTagInBaseValue() {
		check(item1_t1, item1_t1);
		check(item1_t2, item1_t2);
		check(noTags, noTags);
	}

	public void testAggregatesTagsWithCorrectValue() {
		check(Maps.<String, Object> makeImmutableMap("t1", Maps.stringObjectMap(//
				"1_1", item1_t1, //
				"2_1", item2_t1, //
				"3_1", item3_t1), "tag", "irrelevant"), tagsWithCorrectValues);
	}

	public void testIgnoresIncorrectValues() {
		check(tagsWithInCorrectValues, tagsWithInCorrectValues);
		check(Maps.<String, Object> makeImmutableMap("t1", Maps.stringObjectMap("1_1", item1_t1, "2_1", item2_t1, "3_1", item3_t1), "1_2", item1_t2, "tag", "irrelevant"), tagsWithCorrectAndInCorrectValues);
	}

	private void check(Map<String, Object> expected, Map<String, Object> input) {
		Map<String, Object> actual = modifier.modify(cardConfig, "someUrl", input);
		assertEquals(expected, actual);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardConfig = new CardConfig(ICardFactory.Utils.cardFactory(), CardDataStoreFixture.rawCardStore());
	}
}
