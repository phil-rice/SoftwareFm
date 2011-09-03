package org.softwareFm.utilities.maps;

import java.lang.reflect.Array;
import java.util.List;

import org.softwareFm.utilities.annotations.TightLoop;

/**
 * This is on object identity simple map backed by an array. The allowed values of K are defined at the start. It is intended that this is a very fast object to use, and it can easily be pooled.
 */
public class ArraySimpleMap<K, V> implements ISimpleMap<K, V> {
	protected List<K> keys;
	protected V[] values;

	@SuppressWarnings("unchecked")
	public ArraySimpleMap(List<K> keys, Class<V> valueClass) {
		this.keys = keys;
		values = (V[]) Array.newInstance(valueClass, keys.size());
	};

	@TightLoop
	public V get(K key) {
		for (int i = 0; i < keys.size(); i++)
			if (key == keys.get(i))// object equality is very fast compared to equals. So check all the equalities before starting on the equals
				return values[i];
		for (int i = 0; i < keys.size(); i++)
			if (key.equals(keys.get(i)))
				return values[i];
		return null;
	}

	public List<K> keys() {
		return keys;
	}

	/** This is intended to allow the pooling software to reset the values */
	public V[] getValues() {
		return values;
	}

	public void setValuesFrom(List<V> values) {
		for (int i = 0; i < keys.size(); i++)
			this.values[i] = values.get(i);
	}

}
