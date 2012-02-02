/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.pooling;

import java.util.List;

import org.softwareFm.common.maps.ArraySimpleMap;
import org.softwareFm.common.strings.ISimpleStringWithSetters;

/** Each pool manages objects of type T */
public interface IPool<T> extends IPoolThin<T> {

	void prepopulate();

	T newObject();

	static class Utils {

		public static <T> IPool<T> pool(PoolOptions poolOptions, IObjectDefinition<T> defn) {
			IPool<T> result = new Pool<T>(findThinInterface(poolOptions, defn));
			result.prepopulate();
			return result;
		}

		public static <T> IPoolThin<T> findThinInterface(PoolOptions poolOptions, IObjectDefinition<T> defn) {
			return poolOptions.tryToBeThreadSafe ? new ThreadSaferPool<T>(poolOptions, defn) : new ThreadUnsafePool<T>(poolOptions, defn);
		}

		public static IPool<ISimpleStringWithSetters> makeArrayStringPool(PoolOptions poolOptions, final int maxStringLength) {
			IPool<ISimpleStringWithSetters> result = new Pool<ISimpleStringWithSetters>(findThinInterface(poolOptions, IObjectDefinition.Utils.arraySimpleStringDefn(maxStringLength)));
			result.prepopulate();
			return result;
		}

		public static IPool<ISimpleStringWithSetters> makeBufferStringPool(PoolOptions poolOptions, final int maxStringLength) {
			IPool<ISimpleStringWithSetters> result = new Pool<ISimpleStringWithSetters>(findThinInterface(poolOptions, IObjectDefinition.Utils.bufferSimpleStringDefn(maxStringLength)));
			result.prepopulate();
			return result;
		}

		public static <T> IPool<T[]> makeArrayPool(PoolOptions poolOptions, Class<T> clazz, int arrayLength) {
			Pool<T[]> result = new Pool<T[]>(findThinInterface(poolOptions, IObjectDefinition.Utils.arrayDefn(clazz, arrayLength)));
			result.prepopulate();
			return result;
		}

		@SuppressWarnings("unchecked")
		public static <K, V> IPool<ArraySimpleMap<K, V>> makeArraySimpleMapPool(PoolOptions poolOptions, final List<K> keys, final Class<V> valueClass) {
			return makeArraySimpleMapPool(poolOptions, ArraySimpleMap.class, keys, valueClass);
		}

		public static <K, V, T extends ArraySimpleMap<K, V>> IPool<T> makeArraySimpleMapPool(PoolOptions poolOptions, final Class<T> resultClass, final List<K> keys, final Class<V> valueClass) {
			IObjectDefinition<T> defn = IObjectDefinition.Utils.arraySimpleMapDefn(keys, resultClass, valueClass);
			IPool<T> result = new Pool<T>(findThinInterface(poolOptions, defn));
			result.prepopulate();
			return result;
		}

	}

}