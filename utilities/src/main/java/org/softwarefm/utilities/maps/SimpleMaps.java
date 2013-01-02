/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.utilities.maps;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.softwarefm.utilities.collections.Iterables;
import org.softwarefm.utilities.collections.Lists;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.functions.IFunction1;

public class SimpleMaps {

	private static class SimpleMap<K, V> implements ISimpleMap<K, V> {
		private final Map<K, V> map;
		private final List<K> keyList;

		private SimpleMap(Map<K, V> map) {
			this.map = map;
			keyList = Iterables.list(map.keySet());
		}

		public V get(K key) {
			return map.get(key);
		}

		public List<K> keys() {
			return keyList;
		}

		@Override
		public String toString() {
			return map.toString();
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((map == null) ? 0 : map.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SimpleMap other = (SimpleMap) obj;
			if (map == null) {
				if (other.map != null)
					return false;
			} else if (!map.equals(other.map))
				return false;
			return true;
		}
		
	}

	public static <K, V> ISimpleMap<K, V> makeMap(Object... kvs) {
		return fromMap(Maps.<K, V> makeLinkedMap(kvs));
	}

	public static <K, V, T extends Iterable<? extends ISimpleMap<K, V>>> List<Map<K, V>> toListOfMaps(T from) {
		List<Map<K, V>> result = Lists.newList();
		for (ISimpleMap<K, V> map : from)
			result.add(Maps.fromSimpleMap(map));
		return result;
	}

	public static <K, V> ISimpleMap<K, V> fromMap(final Map<K, V> map) {
		return new SimpleMap<K, V>(map);
	}

	public static <K, V> Map<K, V> merge(Iterable<? extends ISimpleMap<K, V>> maps) {
		Map<K, V> result = new HashMap<K, V>();
		for (ISimpleMap<K, V> map : maps)
			for (K key : map.keys())
				result.put(key, map.get(key));
		return result;
	}

	public static <K, V> Map<K, V> merge(ISimpleMap<K, V>... maps) {
		Map<K, V> result = new HashMap<K, V>();
		for (ISimpleMap<K, V> map : maps)
			for (K key : map.keys())
				result.put(key, map.get(key));
		return result;
	}

	public static <K, V> ISimpleMap<K, V> empty() {
		return new ISimpleMap<K, V>() {
			
			public V get(K key) {
				return null;
			}

			
			public List<K> keys() {
				return Collections.emptyList();
			}

		};
	}

	public static <K1, K2, V> List<K2> aggregateKeysOfChildMaps(Map<K1, ? extends ISimpleMap<K2, V>> map) {
		List<K2> result = Lists.newList();
		for (ISimpleMap<K2, V> m : map.values())
			Lists.addAllUnique(result, m.keys());
		return result;
	}

	public static <K, V, P> Map<P, Map<K, V>> partitionByKey(ISimpleMap<K, V> input, IFunction1<K, P> partitionFunction) {
		try {
			Map<P, Map<K, V>> result = Maps.newMap();
			for (K key : input.keys()) {
				V value = input.get(key);
				P partition = partitionFunction.apply(key);
				Maps.addToMapOfLinkedMaps(result, partition, key, value);
			}
			return result;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static <K, V, P> Map<P, Map<K, V>> partitionByValue(ISimpleMap<K, V> input, IFunction1<V, P> partitionFunction) {
		try {
			Map<P, Map<K, V>> result = Maps.newMap();
			for (K key : input.keys()) {
				V value = input.get(key);
				P partition = partitionFunction.apply(value);
				Maps.addToMapOfLinkedMaps(result, partition, key, value);
			}
			return result;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}


}