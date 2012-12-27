package org.softwarefm.eclipse.usage;

import org.softwarefm.eclipse.usage.internal.UsagePersistance;

public interface IUsagePersistance {

	IUsageStats populate(String text);

	String save(IUsageStats usageStats);

	public static class Utils {
		public static IUsagePersistance persistance() {
			return new UsagePersistance();
		}
	}
}
