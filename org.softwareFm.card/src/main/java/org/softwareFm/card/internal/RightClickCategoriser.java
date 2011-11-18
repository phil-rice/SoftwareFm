package org.softwareFm.card.internal;

import java.util.Map;

import org.softwareFm.card.api.IRightClickCategoriser;
import org.softwareFm.card.api.RightClickCategoryResult;
import org.softwareFm.card.api.RightClickCategoryResult.Type;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.utilities.strings.Strings;

public class RightClickCategoriser implements IRightClickCategoriser {

	@Override
	public RightClickCategoryResult categorise(String url, Map<String, Object> map, String key) {
		String lastSegment = Strings.lastSegment(url, "/");
		String cardType = (String) map.get(CardConstants.slingResourceType);
		Object value = map == null ? null : map.get(key);
		if (CardConstants.collection.equals(cardType))
			return new RightClickCategoryResult(Type.ROOT_COLLECTION, lastSegment, key, url);
		else if (CardConstants.ntUnstructured.equals(key)) // clicked on item representing key
			return new RightClickCategoryResult(Type.IS_FOLDERS, CardConstants.folder, key, url);
		else if (cardType != null && value instanceof Map<?, ?>)
			return new RightClickCategoryResult(Type.IS_COLLECTION, key, key, url);
		else
			return new RightClickCategoryResult(Type.NOT_COLLECTION, null, key, url);
	}

}
