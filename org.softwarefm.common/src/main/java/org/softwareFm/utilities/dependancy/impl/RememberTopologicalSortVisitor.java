/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.utilities.dependancy.impl;

import java.util.List;
import java.util.Map;

import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.dependancy.ITopologicalSortResultVisitor;
import org.softwareFm.utilities.maps.Maps;

public class RememberTopologicalSortVisitor<T> implements ITopologicalSortResultVisitor<T> {
	private final Map<T, Integer> actual = Maps.newMap();
	private final List<Integer> generations = Lists.newList();

	@Override
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