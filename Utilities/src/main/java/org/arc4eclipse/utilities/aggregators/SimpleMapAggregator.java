package org.arc4eclipse.utilities.aggregators;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.arc4eclipse.utilities.maps.ISimpleMap;
import org.arc4eclipse.utilities.maps.SimpleMaps;

public class SimpleMapAggregator<K, V> implements IAggregator<ISimpleMap<K, V>, ISimpleMap<K, V>> {

	private final Map<K, V> result = Collections.synchronizedMap(new HashMap<K, V>());

	public ISimpleMap<K, V> result() {
		return SimpleMaps.fromMap(result);
	}

	public void add(ISimpleMap<K, V> t) {
		for (K key : t.keys())
			result.put(key, t.get(key));
	}

}
