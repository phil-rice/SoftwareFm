package org.softwarefm.eclipse.usage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.softwarefm.utilities.maps.ISimpleMap;

public interface IUsageStats extends ISimpleMap<String, UsageStatData>{

	public final static class Utils{
		public static IUsageStats fromMap(final Map<String, UsageStatData> map){
			return new IUsageStats() {
				public List<String> keys() {
					return new ArrayList<String>(map.keySet());
				}
				
				public UsageStatData get(String key) {
					return map.get(key);
				}
			};
		}
	}
	
}
