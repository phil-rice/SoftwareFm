package org.softwarefm.shared.usage;

public interface IUsage {

	void selected(String usage);

	IUsageStats getStats();

	void setUsage(String usage, UsageStatData usageStatData);

	void nuke();

}