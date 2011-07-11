package org.arc4eclipse.utilities.maps;

import java.util.List;

public interface ISimpleMap<K, V> {

	V get(K key);

	List<K> keys();

}
