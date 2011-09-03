package org.softwareFm.utilities.dependancy.impl;

import java.util.List;
import java.util.Map;

import org.softwareFm.utilities.collections.ISimpleList;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.collections.SimpleLists;
import org.softwareFm.utilities.dependancy.ITopologicalSortResult;

public class TopologicalSortResult<T> implements ITopologicalSortResult<T> {

	private final List<ISimpleList<T>> list;

	public TopologicalSortResult(Map<Integer, List<T>> generationMap, int maxGeneration) {
		this.list = Lists.newList();
		for (int i = 0; i <= maxGeneration; i++) {
			ISimpleList<T> generation = SimpleLists.fromList(generationMap.get(i));
			list.add(generation);
		}
	}

	public int size() {
		return list.size();
	}

	public ISimpleList<T> get(int index) {
		return list.get(index);
	}

}
