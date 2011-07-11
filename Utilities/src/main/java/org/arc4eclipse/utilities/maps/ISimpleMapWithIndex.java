package org.arc4eclipse.utilities.maps;

public interface ISimpleMapWithIndex<K, V> extends ISimpleMap<K, V> {

	V getByIndex(int keyIndex);
}
