/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.collections.internal;

import java.util.Map;

import org.softwareFm.card.card.IRightClickCategoriser;
import org.softwareFm.card.card.RightClickCategoryResult;
import org.softwareFm.card.card.RightClickCategoryResult.Type;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.common.strings.Strings;

public class SoftwareFmRightClickCategoriser implements IRightClickCategoriser {

	@Override
	public RightClickCategoryResult categorise(String url, Map<String, Object> map, String key) {
		String lastSegment = Strings.lastSegment(url, "/");
		String cardType = (String) map.get(CardConstants.slingResourceType);
		Object value = map == null ? null : map.get(key);
		if (cardType == null && key == null)
			return new RightClickCategoryResult(Type.COLLECTION_NOT_CREATED_YET, lastSegment, null, url);
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