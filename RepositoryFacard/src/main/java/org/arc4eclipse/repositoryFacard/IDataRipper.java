package org.arc4eclipse.repositoryFacard;

import java.util.Map;

import org.arc4eclipse.utilities.maps.Maps;

public interface IDataRipper<Data> {

	<T> T get(Data data, String... keys);

	<T> void put(Data data, String[] keys, T value);

	public static class Utils {
		public static IDataRipper<Map<Object, Object>> mapRipper() {
			return new IDataRipper<Map<Object, Object>>() {
				@SuppressWarnings("unchecked")
				
				public <T> T get(Map<Object, Object> data, String... keys) {
					return (T) Maps.get(data, (Object[]) keys);
				}

				
				public <T> void put(Map<Object, Object> data, String[] keys, T value) {
					Maps.put(data, keys, value);
				}

			};
		}
	}
}
