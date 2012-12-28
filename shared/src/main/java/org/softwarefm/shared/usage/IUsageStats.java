package org.softwarefm.shared.usage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.softwarefm.utilities.maps.ISimpleMap;
import org.softwarefm.utilities.maps.Maps;

public interface IUsageStats extends ISimpleMap<String, UsageStatData> {

	public final static class Utils {
		private static class UsageStats implements IUsageStats {
			private final Map<String, UsageStatData> map;

			private UsageStats(Map<String, UsageStatData> map) {
				this.map = map;
			}

			public List<String> keys() {
				return new ArrayList<String>(map.keySet());
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
			return fromMap( Maps.<String, UsageStatData> makeMap(nameAndStats));
		}
	}

}