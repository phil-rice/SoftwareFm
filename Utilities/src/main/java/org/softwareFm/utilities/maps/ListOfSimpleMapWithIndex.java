/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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