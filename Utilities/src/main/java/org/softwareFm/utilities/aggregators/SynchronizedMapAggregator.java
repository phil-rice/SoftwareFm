package org.softwareFm.utilities.aggregators;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SynchronizedMapAggregator<K, V> implements IAggregator<Map<K, V>, Map<K, V>> {

	private final Map<K, V> result = Collections.synchronizedMap(new HashMap<K, V>());

	public void add(Map<K, V> t) {
		result.putAll(t);

	}

	public Map<K, V> result() {
		return result;
	}

}
