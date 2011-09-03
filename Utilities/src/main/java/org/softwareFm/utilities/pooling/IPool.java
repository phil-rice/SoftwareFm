package org.softwareFm.utilities.pooling;

import java.util.List;

import org.softwareFm.utilities.maps.ArraySimpleMap;
import org.softwareFm.utilities.strings.ISimpleStringWithSetters;

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
