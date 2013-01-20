package org.softwarefm.shared.usage;

import java.util.ArrayList;
import java.util.Map;

import org.softwarefm.utilities.functions.IFunction1;
import org.softwarefm.utilities.maps.ISimpleMap;
import org.softwarefm.utilities.maps.Maps;

public interface IUsageStats extends ISimpleMap<String, UsageStatData> {

	public final static class Utils {
		private static class UsageStats implements IUsageStats {
			private final Map<String, UsageStatData> map;
			private final ArrayList<String> keyset;

			private UsageStats(Map<String, UsageStatData> map) {
				this.map = map;
				this.keyset = new ArrayList<String>(map.keySet());
			}

			@Override
			public int size() {
				return map.size();
			}

			@Override
			public String key(int i) {
				return keyset.get(i);
			}

			public UsageStatData get(String key) {
				return map.get(key);
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
				UsageStats other = (UsageStats) obj;
				if (map == null) {
					if (other.map != null)
						return false;
				} else if (!map.equals(other.map))
					return false;
				return true;
			}

			@Override
			public String toString() {
				return "UsageStats [map=" + map + "]";
			}
		}

		public static IUsageStats fromMap(final Map<String, UsageStatData> map) {
			return new UsageStats(map);
		}

		public static IUsageStats from(Object... nameAndStats) {
			return fromMap(Maps.<String, UsageStatData> makeMap(nameAndStats));
		}

		public static IUsageStats fromIntegerMap(Map<String, Integer> result) {
			return fromMap(Maps.mapTheMap(result, new IFunction1<Integer, UsageStatData>() {
				@Override
				public UsageStatData apply(Integer from) throws Exception {
					return new UsageStatData(from);
				}
			}));
		}

		private static IUsageStats empty = new IUsageStats() {
			@Override
			public String key(int i) {
				throw new IllegalArgumentException();
			}

			public int size() {
				return 0;
			}

			@Override
			public UsageStatData get(String key) {
				return null;
			}
		};

		public static IUsageStats empty() {
			return empty;
		}
	}

}
