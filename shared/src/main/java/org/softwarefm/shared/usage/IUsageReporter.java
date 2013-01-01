package org.softwarefm.shared.usage;

import org.softwarefm.shared.usage.internal.UsageReporter;
import org.softwarefm.utilities.http.IHttpClient;

public interface IUsageReporter {

	void report(String user, IUsageStats usage);

	public static class Utils {

		public static IUsageReporter reporter(String host, int port) {
			return new UsageReporter(IUsagePersistance.Utils.persistance(), IHttpClient.Utils.builder().host(host, port));
		}
		public static IUsageReporter reporter() {
			return reporter(UsageConstants.host, UsageConstants.port);
		}
	}
}
