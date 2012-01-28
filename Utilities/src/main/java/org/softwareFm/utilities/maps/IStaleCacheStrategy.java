package org.softwareFm.utilities.maps;

import java.util.Map;

public interface IStaleCacheStrategy<K, V> {

	boolean needToRecreate(Map<K, V> map, K key);

	public static class Utils {
		public static <K, V> IStaleCacheStrategy<K, V> allwaysRecreate() {
			return new IStaleCacheStrategy<K, V>() {
				@Override
				public boolean needToRecreate(Map<K, V> map, K key) {
					return true;
				}
			};
		}

		public static <K, V> IStaleCacheStrategy<K, V> neverRecreate() {
			return new IStaleCacheStrategy<K, V>() {
				@Override
				public boolean needToRecreate(Map<K, V> map, K key) {
					return !map.containsKey(key);
				}
			};
		}

		public static <K, V> IStaleCacheStrategy<K, V> timer(final long periodInMs) {
			return new IStaleCacheStrategy<K, V>() {
				private long lastTime = System.currentTimeMillis();

				@Override
				public boolean needToRecreate(Map<K, V> map, K key) {
					long now = System.currentTimeMillis();
					boolean result = !map.containsKey(key) || now > lastTime + periodInMs;
					if (result)
						lastTime = now;
					return result;
				}
			};
		}
	}
}
