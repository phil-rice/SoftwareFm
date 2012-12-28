package org.softwarefm.server.usage;

import org.softwarefm.shared.usage.IUsageStats;

public interface IUsageGetter {
	IUsageStats getStats(String user);
}
