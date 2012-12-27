package org.softwarefm.eclipse.usage;



public interface IUsage {

	void selected(String usage);

	IUsageStats getStats();

	void setUsage(String usage, UsageStatData usageStatData);

	void nuke();

	void addUsageListener(IUsageListener listener);

	void fireListeners();
}
