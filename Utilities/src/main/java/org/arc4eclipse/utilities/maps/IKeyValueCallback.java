package org.arc4eclipse.utilities.maps;

public interface IKeyValueCallback<K, V> {

	void process(K key, V value);

}
