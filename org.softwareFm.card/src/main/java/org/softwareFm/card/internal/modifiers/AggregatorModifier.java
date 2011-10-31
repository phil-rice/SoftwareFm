package org.softwareFm.card.internal.modifiers;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataModifier;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Strings;

public class AggregatorModifier implements ICardDataModifier {

	private final List<String> tagNames;

	public AggregatorModifier(List<String> tagNames) {
		this.tagNames = tagNames;
	}

	@Override
	public Map<String, Object> modify(CardConfig cardConfig, ICard card, Map<String, Object> rawData) {
		Map<String, Map<String, Object>> aggregates = Maps.newMap(LinkedHashMap.class);
		Map<String, Object> result = Maps.newMap();
		for (Entry<String, Object> entry : rawData.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof Map) {
				Map<String, Object> valueMap = (Map<String, Object>) value;
				String tag = findAnyTag(valueMap);
				if (tag == null)
					result.put(key, value);
				else
					Maps.addToMapOfMaps(aggregates, HashMap.class, tag, key, value);
			} else
				result.put(key, value);
		}
		result.putAll(aggregates);// note that this means that we cannot have a key with the same name as a tag.
		return result;
	}

	private String findAnyTag(Map<String, Object> valueMap) {
		for (String tagName : tagNames) {
			Object value = valueMap.get(tagName);
			if (value != null)
				return Strings.nullSafeToString(value);
		}
		return null;
	}

}
