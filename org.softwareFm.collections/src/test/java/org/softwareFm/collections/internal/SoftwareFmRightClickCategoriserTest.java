package org.softwareFm.collections.internal;

import java.util.Map;

import junit.framework.TestCase;

import org.softwareFm.card.card.IRightClickCategoriser;
import org.softwareFm.card.card.RightClickCategoryResult;
import org.softwareFm.card.card.RightClickCategoryResult.Type;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.utilities.maps.Maps;

public class SoftwareFmRightClickCategoriserTest extends TestCase {

	private final IRightClickCategoriser categoriser = new SoftwareFmRightClickCategoriser();

	public void test() {
		check("a/b/c", "artifact", "tweet", null, null, Type.NOT_COLLECTION); // no map as the value
		check("a/b/c", "artifact", "tweet", Maps.stringObjectMap(), "tweet", Type.IS_COLLECTION);
		check("a/b/c", CardConstants.collection, "tweetUrlFragment", Maps.stringObjectMap(), "c", Type.ROOT_COLLECTION);
		check("a/b/c", null, CardConstants.ntUnstructured, Maps.stringObjectMap(), CardConstants.folder, Type.IS_FOLDERS);
		check("a/b/c", null, null, null, "c", Type.COLLECTION_NOT_CREATED_YET);
	}

	private void check(String url, String resouceType, String key, Map<String, Object> value, String expectedCollectionName, Type expectedItemType) {
		Map<String, Object> data = Maps.stringObjectMap(CardConstants.slingResourceType, resouceType, key, value);
		RightClickCategoryResult result = categoriser.categorise(url, data, key);
		assertEquals(key, result.key);
		assertEquals(expectedCollectionName, result.collectionName);
		assertEquals(url, result.url);
		assertEquals(expectedItemType, result.itemType);
	}

}
