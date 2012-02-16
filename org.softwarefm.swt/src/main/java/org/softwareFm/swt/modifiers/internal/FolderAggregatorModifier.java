/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.modifiers.internal;

import java.util.Map;
import java.util.Map.Entry;

import org.softwareFm.common.maps.Maps;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.modifiers.ICardDataModifier;

public class FolderAggregatorModifier implements ICardDataModifier {

	private final String tagName;
	private final String lookedFor;
	private final String ignoreTag;

	public FolderAggregatorModifier(String tagName, String lookedFor, String ignoreTag) {
		this.tagName = tagName;
		this.lookedFor = lookedFor;
		this.ignoreTag = ignoreTag;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> modify(CardConfig cardConfig, String url, Map<String, Object> rawData) {
		Map<String, Object> result = Maps.newMap();
		Map<String, Object> aggregates = Maps.newMap();
		for (Entry<String, Object> entry : rawData.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof Map) {
				Map<String, Object> valueMap = (Map<String, Object>) value;
				Object tag = valueMap.get(tagName);

				if (!valueMap.containsKey(ignoreTag) && lookedFor.equals(tag)) {
					aggregates.put(key, value);
					continue;
				}
			}
			result.put(key, value);
		}
		if (aggregates.size() > 0)
			result.put(lookedFor, aggregates);// note that this means that we cannot have a key with the same name as a tag.
		return result;
	}

}