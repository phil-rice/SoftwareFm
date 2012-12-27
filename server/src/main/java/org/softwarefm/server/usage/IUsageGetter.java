package org.softwarefm.server.usage;

import org.softwarefm.eclipse.usage.IUsageStats;

public interface IUsageGetter {
	IUsageStats getStats(String user);
}
