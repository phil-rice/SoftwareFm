package org.softwareFm.display.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.softwareFm.utilities.maps.ISimpleMap;
import org.softwareFm.utilities.maps.Maps;

public interface IUrlGeneratorMap extends ISimpleMap<String, IUrlGenerator> {

	public static class Utils {
		public static IUrlGeneratorMap urlGeneratorMap(Object... nameAndGenerators) {
			return urlGeneratorMap(Maps.<String, IUrlGenerator> makeMap(nameAndGenerators));
		}

		public static IUrlGeneratorMap urlGeneratorMap(final Map<String, IUrlGenerator> map) {
			return new IUrlGeneratorMap() {

				private List<String> keys;

				@Override
				public IUrlGenerator get(String key) {
					return map.get(key);
				}

				@Override
				public List<String> keys() {
					if (keys == null)
						keys = new ArrayList<String>(map.keySet());
					return keys;
				}

				@Override
				public String toString() {
					return map.toString();
				}

			};
		}
	}
}