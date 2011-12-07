package org.softwareFm.card.modifiers.internal;

import java.util.Map;
import java.util.Map.Entry;

import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.modifiers.ICardDataModifier;
import org.softwareFm.utilities.maps.Maps;

public class HideColectionsModifier implements ICardDataModifier {

	@Override
	public Map<String, Object> modify(CardConfig cardConfig, String url, Map<String, Object> rawData) {
		Map<String, Object> result = Maps.copyMap(rawData);
		for (Entry<String, Object> entry: rawData.entrySet())
			if (entry.getValue() instanceof Map<?,?>)
				result.remove(entry.getKey());
		return result;
	}

}
