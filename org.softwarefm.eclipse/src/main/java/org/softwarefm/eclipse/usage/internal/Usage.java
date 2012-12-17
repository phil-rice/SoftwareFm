package org.softwarefm.eclipse.usage.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwarefm.eclipse.usage.IUsage;
import org.softwarefm.eclipse.usage.IUsageListener;
import org.softwarefm.eclipse.usage.UsageStats;
import org.softwarefm.utilities.functions.IFunction1;
import org.softwarefm.utilities.maps.Maps;

public class Usage implements IUsage {

	private final Map<String, AtomicInteger> data = Maps.makeMap();
	private final List<IUsageListener> listeners = Collections.synchronizedList(new ArrayList<IUsageListener>());

	public void selected(String usage) {
		Maps.findOrCreate(data, usage, new Callable<AtomicInteger>() {
			public AtomicInteger call() throws Exception {
				return new AtomicInteger();
			}
		}).incrementAndGet();
		for (IUsageListener listener: listeners)
			listener.usageOccured(usage);
	}

	public Map<String, UsageStats> getStats() {
		return Maps.mapTheMap(data, new IFunction1<AtomicInteger, UsageStats>() {
			public UsageStats apply(AtomicInteger from) throws Exception {
				return new UsageStats(from.get());
			}
		});
	}

	public void setUsageStat(String path, UsageStats usageStats) {
		synchronized (data) {
			data.put(path, new AtomicInteger(usageStats.count));
		}
	}

	public void addUsageListener(IUsageListener listener) {
		listeners.add(listener);

	}

}
