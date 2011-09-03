package org.softwareFm.utilities.dependancy.impl;

import java.util.List;
import java.util.Map;

import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.dependancy.ITopologicalSortResultVisitor;
import org.softwareFm.utilities.maps.Maps;

public class RememberTopologicalSortVisitor<T> implements ITopologicalSortResultVisitor<T> {
	private final Map<T, Integer> actual = Maps.newMap();
	private final List<Integer> generations = Lists.newList();

	public void visit(int generation, T item) {
		actual.put(item, generation);
		generations.add(generation);
	}

	public Map<T, Integer> getActual() {
		return actual;
	}

	public List<Integer> getGenerations() {
		return generations;
	}

}