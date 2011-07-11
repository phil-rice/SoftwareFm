package org.arc4eclipse.utilities.functions;

import java.util.HashMap;
import java.util.Map;

public class MapFoldFunction<K, V> implements ISymmetricFunction<Map<K, V>> {

	public Map<K, V> apply(Map<K, V> value, Map<K, V> initial) {
		Map<K, V> result = new HashMap<K, V>();
		result.putAll(value);
		result.putAll(initial);
		return result;
	}

}
