package org.softwarefm.eclipse.usage;

import java.util.Map;

public interface IUsage {

	void selected(String usage);
	
	Map<String,UsageStats> getStats();
	
	void addUsageListener(IUsageListener listener);
}
