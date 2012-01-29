/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.modifiers.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.modifiers.ICardDataModifier;
import org.softwareFm.utilities.maps.Maps;

public class CollectionsAggregatorModifier implements ICardDataModifier {

	private final String tagName;

	public CollectionsAggregatorModifier(String tagName) {
		this.tagName = tagName;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> modify(CardConfig cardConfig, String url, Map<String, Object> rawData) {
		Map<String, Object> result = Maps.newMap();
		Map<String, Map<String, Object>> aggregates = Maps.newMap();
		for (Entry<String, Object> entry : rawData.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof Map) {
				Map<String, Object> valueMap = (Map<String, Object>) value;
				Object tag = valueMap.get(tagName);
				if (tag == null)
					result.put(key, value);
				else
					Maps.addToMapOfMaps(aggregates, HashMap.class, (String) tag, key, value);
			} else
				result.put(key, value);
		}
		for (Entry<String, Map<String, Object>> entry : aggregates.entrySet())
			result.putAll(entry.getValue());
		return result;
	}

}