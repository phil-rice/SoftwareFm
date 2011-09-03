package org.softwareFm.utilities.maps;

import java.util.List;

import org.softwareFm.utilities.collections.Lists;

public class ListOfSimpleMapWithIndex<K, V> implements IListOfSimpleMapWithIndex<K, V> {

	private final List<ISimpleMapWithIndex<K, V>> list = Lists.newList();
	private final List<K> keys;

	public ListOfSimpleMapWithIndex(List<K> keys) {
		this.keys = keys;
	}

	public void add(ISimpleMapWithIndex<K, V> map) {
		list.add(map);
	}

	public int size() {
		return list.size();
	}

	public ISimpleMapWithIndex<K, V> get(int i) {
		return list.get(i);
	}

	public List<K> keys() {
		return keys;
	}
}
