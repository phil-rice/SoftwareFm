package org.softwarefm.shared.usage;

import org.softwarefm.shared.usage.internal.UsagePersistance;
import org.softwarefm.utilities.maps.ISimpleMap;

public interface IUsagePersistance {

	IUsageStats parse(String text);

	String save(IUsageStats usageStats);

	String save(ISimpleMap<String, IUsageStats> friendsUsage);

	ISimpleMap<String, IUsageStats> parseFriendsUsage(String text);

	
	public static class Utils {
		public static IUsagePersistance persistance() {
			return new UsagePersistance();
		}
	}
}