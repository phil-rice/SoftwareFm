/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.collections.internal;

import java.util.Map;

import junit.framework.TestCase;

import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.swt.card.IRightClickCategoriser;
import org.softwareFm.swt.card.RightClickCategoryResult;
import org.softwareFm.swt.card.RightClickCategoryResult.Type;

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