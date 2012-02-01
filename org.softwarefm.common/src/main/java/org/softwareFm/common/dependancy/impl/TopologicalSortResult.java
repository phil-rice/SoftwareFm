/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.dependancy.impl;

import java.util.List;
import java.util.Map;

import org.softwareFm.common.collections.ISimpleList;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.collections.SimpleLists;
import org.softwareFm.common.dependancy.ITopologicalSortResult;

public class TopologicalSortResult<T> implements ITopologicalSortResult<T> {

	private final List<ISimpleList<T>> list;

	public TopologicalSortResult(Map<Integer, List<T>> generationMap, int maxGeneration) {
		this.list = Lists.newList();
		for (int i = 0; i <= maxGeneration; i++) {
			ISimpleList<T> generation = SimpleLists.fromList(generationMap.get(i));
			list.add(generation);
		}
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public ISimpleList<T> get(int index) {
		return list.get(index);
	}

}