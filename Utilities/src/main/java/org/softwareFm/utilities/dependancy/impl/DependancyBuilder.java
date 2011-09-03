package org.softwareFm.utilities.dependancy.impl;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.constants.UtilityMessages;
import org.softwareFm.utilities.dependancy.IDependancyBuilder;
import org.softwareFm.utilities.dependancy.ITopologicalSortResult;
import org.softwareFm.utilities.exceptions.LoopException;
import org.softwareFm.utilities.maps.Maps;

public class DependancyBuilder<T> implements IDependancyBuilder<T> {

	private final Map<T, Set<T>> map = Maps.newMap();

	public IDependancyBuilder<T> parent(T child, T parent) {
		if (dependsOn(parent, child))
			throw new LoopException(MessageFormat.format(UtilityMessages.loopException, child, parent, path(parent, child)));
		Maps.addToCollection(map, HashSet.class, parent, child);
		return this;
	}

	public List<T> path(T child, T parent) {
		List<T> path = Lists.newList();
		addPath(child, parent, path);
		return path;
	};

	public boolean addPath(T child, T parent, List<T> path) {
		boolean direct = directDependant(child, parent);
		if (direct) {
			path.add(child);
			path.add(parent);
			return true;
		}
		Set<T> children = map.get(parent);
		if (children != null)
			for (T t : children) {
				if (addPath(child, t, path)) {
					path.add(parent);
					return true;
				}
			}
		return false;
	}

	public boolean dependsOn(T child, T parent) {
		boolean direct = directDependant(child, parent);
		if (direct)
			return true;
		Set<T> children = map.get(parent);
		if (children != null)
			for (T t : children)
				if (dependsOn(child, t))
					return true;
		return false;
	}

	private boolean directDependant(T child, T parent) {
		Set<T> children = map.get(parent);
		if (children == null)
			return false;
		return children.contains(child);
	}

	public ITopologicalSortResult<T> sort() {
		Map<T, Integer> generationMap = Maps.newMap();
		int maxGeneration = Integer.MIN_VALUE;
		for (int i = 0; i < map.size() + 1; i++)
			for (Entry<T, Set<T>> entry : map.entrySet()) {
				T parent = entry.getKey();
				for (T child : entry.getValue()) {
					int parentGeneration = Maps.intFor(generationMap, parent);
					int currentChildGeneration = Maps.intFor(generationMap, child);
					int childGeneration = Math.max(currentChildGeneration, parentGeneration + 1);
					generationMap.put(parent, parentGeneration);
					generationMap.put(child, childGeneration);
					maxGeneration = Math.max(maxGeneration, childGeneration);
				}
			}
		Map<Integer, List<T>> inverseGenerationMap = Maps.inverse(generationMap);
		return new TopologicalSortResult<T>(inverseGenerationMap, maxGeneration);
	}
}
