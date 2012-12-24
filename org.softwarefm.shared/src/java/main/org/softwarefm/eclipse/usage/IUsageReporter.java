package org.softwarefm.eclipse.usage;

import org.softwarefm.eclipse.usage.internal.UsageReporter;

public interface IUsageReporter {

	void report(IUsage usage);

	public static class Utils {
		public static IUsageReporter reporter(String host, int port) {
			return new UsageReporter(IUsagePersistance.Utils.persistance(), host, port, UsageConstants.url);
		}
		public static IUsageReporter reporter() {
			return new UsageReporter(IUsagePersistance.Utils.persistance(), UsageConstants.host, UsageConstants.port, UsageConstants.url);
		}
	}
}