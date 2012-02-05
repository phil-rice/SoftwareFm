/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.internal.modifiers;

import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.swt.card.CardDataStoreFixture;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.modifiers.internal.FolderAggregatorModifier;

public class FolderAggregatorModifierTest extends TestCase {
	// for example tag is jcr:primarytype and the looked for value is nt:unstructured

	private final static Map<String, Object> item1_1 = Maps.makeImmutableMap("item1", 1);
	private final static Map<String, Object> item1_2 = Maps.makeImmutableMap("item1", 2);

	private final static Map<String, Object> item1_t1 = Maps.makeImmutableMap("item1", 1, "tag", "t1");
	private final static Map<String, Object> item2_t1 = Maps.makeImmutableMap("item2", 1, "tag", "t1");
	private final static Map<String, Object> item3_t1 = Maps.makeImmutableMap("item1", 2, "tag", "t1");
	private final static Map<String, Object> item1_t2 = Maps.makeImmutableMap("item2", 2, "tag", "t2");

	private final static Map<String, Object> noTags = Maps.makeImmutableMap("1_1", item1_1, "1_2", item1_2, "tag", "coll1");
	private final static Map<String, Object> tagsWithCorrectValues = Maps.makeImmutableMap("1_1", item1_t1, "2_1", item2_t1, "3_1", item3_t1, "tag", "irrelevant");
	private final static Map<String, Object> tagsWithInCorrectValues = Maps.makeImmutableMap("1_1", item1_t2, "junk", 1);
	private final static Map<String, Object> tagsWithCorrectAndInCorrectValues = Maps.makeImmutableMap("1_1", item1_t1, "2_1", item2_t1, "3_1", item3_t1, "tag", "irrelevant", "1_2", item1_t2);

	private final FolderAggregatorModifier modifier = new FolderAggregatorModifier("tag", "t1", "ignoreThisTag");
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
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		cardConfig.dispose();
	}
}