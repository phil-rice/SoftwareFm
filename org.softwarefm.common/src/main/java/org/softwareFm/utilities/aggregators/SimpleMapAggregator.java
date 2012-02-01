/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.utilities.aggregators;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.softwareFm.utilities.maps.ISimpleMap;
import org.softwareFm.utilities.maps.SimpleMaps;

public class SimpleMapAggregator<K, V> implements IAggregator<ISimpleMap<K, V>, ISimpleMap<K, V>> {

	private final Map<K, V> result = Collections.synchronizedMap(new HashMap<K, V>());

	@Override
	public ISimpleMap<K, V> result() {
		return SimpleMaps.fromMap(result);
	}

	@Override
	public void add(ISimpleMap<K, V> t) {
		for (K key : t.keys())
			result.put(key, t.get(key));
	}

}