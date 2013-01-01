package org.softwarefm.shared.usage;

import org.softwarefm.shared.social.ISocialManager;
import org.softwarefm.shared.usage.internal.UsagePersistance;

public interface IUsagePersistance {

	IUsageStats parse(String text);

	String saveUsageStats(IUsageStats usageStats);

	String save(ISocialManager manager);
	
	void populate(ISocialManager manager, String saved);

	public static class Utils {
		public static IUsagePersistance persistance() {
			return new UsagePersistance();
		}
	}

}
