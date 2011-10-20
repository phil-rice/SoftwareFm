package org.softwareFm.card.internal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.softwareFm.card.api.KeyValue;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Strings;

public class KeyValueAggregator implements IFunction1<Map<String, Object>, List<KeyValue>> {

	private final List<String> tagNames;

	public KeyValueAggregator(List<String> tagNames) {
		this.tagNames = tagNames;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<KeyValue> apply(Map<String, Object> rawMap) throws Exception {
		List<KeyValue> result = Lists.newList();
		Map<String, List<Object>> aggregates = Maps.newMap(LinkedHashMap.class);
		for (Entry<String, Object> entry : rawMap.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof Map) {
				Map<String, Object> valueMap = (Map<String, Object>) value;
				String tag = findAnyTag(valueMap);
				if (tag == null)
					result.add(new KeyValue(key, value));
				else
					Maps.addToList(aggregates, tag, new KeyValue(key, value));
			} else
				result.add(new KeyValue(key, value));

		}
		for (Entry<String, List<Object>> entry : aggregates.entrySet())
			result.add(new KeyValue(entry.getKey(), entry.getValue()));
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
