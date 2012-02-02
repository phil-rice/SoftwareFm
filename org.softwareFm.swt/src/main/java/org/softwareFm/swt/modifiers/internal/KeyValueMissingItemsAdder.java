/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.modifiers.internal;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.modifiers.ICardDataModifier;

public class KeyValueMissingItemsAdder implements ICardDataModifier {

	@Override
	public Map<String, Object> modify(CardConfig cardConfig, String url, Map<String, Object> rawData) {
		String cardType = (String) rawData.get(CardConstants.slingResourceType);
		List<String> missingStrings = Strings.splitIgnoreBlanks(IResourceGetter.Utils.get(cardConfig.resourceGetterFn, cardType, CardConstants.missingStringKey), ",");
		List<String> missingLists = Strings.splitIgnoreBlanks(IResourceGetter.Utils.get(cardConfig.resourceGetterFn, cardType, CardConstants.missingListKey), ",");
		missingLists.removeAll(rawData.keySet());
		missingStrings.removeAll(rawData.keySet());
		missingStrings.removeAll(missingLists); // just in case
		if (!missingLists.isEmpty() || !missingStrings.isEmpty()) {
			Map<String, Object> result = Maps.copyMap(rawData);
			for (String item : missingStrings) {
				String value = IResourceGetter.Utils.getOr(cardConfig.resourceGetterFn, cardType, "missing." + item + ".default", "");
				result.put(item, value);
			}
			for (String item : missingLists)
				result.put(item, Collections.emptyMap());
			return result;
		}
		return rawData;
	}
}