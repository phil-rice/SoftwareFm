package org.softwareFm.card.internal.modifiers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataModifier;
import org.softwareFm.utilities.maps.Maps;

public class CollectionsAggregatorModifier implements ICardDataModifier {

	private final String tagName;

	public CollectionsAggregatorModifier(String tagName) {
		this.tagName = tagName;
	}

	@Override
	public Map<String, Object> modify(CardConfig cardConfig, ICard card, Map<String, Object> rawData) {
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
		result.putAll(aggregates);// note that this means that we cannot have a key with the same name as a tag.
		return result;
	}

}