package org.softwareFm.card.internal.modifiers;

import java.util.Map;
import java.util.Map.Entry;

import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataModifier;
import org.softwareFm.utilities.maps.Maps;

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
	public Map<String, Object> modify(CardConfig cardConfig, ICard card, Map<String, Object> rawData) {
		Map<String, Object> result = Maps.newMap();
		Map<String, Object> aggregates = Maps.newMap();
		for (Entry<String, Object> entry : rawData.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof Map) {
				Map<String, Object> valueMap = (Map<String, Object>) value;
				Object tag = valueMap.get(tagName);
				
				if (!valueMap.containsKey(ignoreTag)&&lookedFor.equals(tag)) {
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
