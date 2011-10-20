package org.softwareFm.card.internal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.softwareFm.card.api.KeyValue;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;

public class KeyValueAggregator implements IFunction1<Map<String, Object>, List<KeyValue>> {

	private final String tagName;

	public KeyValueAggregator(String tagName) {
		this.tagName = tagName;
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
				String tag = (String) valueMap.get(tagName);
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

}
