package org.softwarefm.eclipse.usage;

import java.util.Map;

public interface IUsage {

	void selected(String usage);

	Map<String, UsageStats> getStats();

	void setUsage(String usage, UsageStats usageStats);

	void nuke();

	void addUsageListener(IUsageListener listener);

	void fireListeners();
}
