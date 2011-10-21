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
	private final List<String> urlParts;

	public KeyValueAggregator(List<String> tagNames, List<String> urlParts) {
		this.tagNames = tagNames;
		this.urlParts = urlParts;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<KeyValue> apply(Map<String, Object> rawMap) throws Exception {
		List<KeyValue> result = Lists.newList();
		Map<String, List<Object>> aggregates = Maps.newMap(LinkedHashMap.class);
		for (Entry<String, Object> entry : rawMap.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			KeyValue keyValue = new KeyValue(key, value);
			if (value instanceof Map) {
				if (urlParts.contains(key))
					Maps.addToList(aggregates, key, keyValue);
				else {
					Map<String, Object> valueMap = (Map<String, Object>) value;
					String tag = findAnyTag(valueMap);
					if (tag == null)
						result.add(keyValue);
					else
						Maps.addToList(aggregates, tag, keyValue);
				}
			} else
				result.add(keyValue);
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
