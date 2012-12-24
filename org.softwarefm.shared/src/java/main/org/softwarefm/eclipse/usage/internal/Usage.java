package org.softwarefm.eclipse.usage.internal;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwarefm.eclipse.usage.IUsage;
import org.softwarefm.eclipse.usage.IUsageListener;
import org.softwarefm.eclipse.usage.UsageStats;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.events.IListenerList;
import org.softwarefm.utilities.events.IMultipleListenerList;
import org.softwarefm.utilities.functions.IFunction1;
import org.softwarefm.utilities.maps.Maps;

public class Usage implements IUsage {

	private final Map<String, AtomicInteger> data = Maps.makeMap();
	private final IListenerList<IUsageListener> listeners;

	public Usage(IMultipleListenerList listenerList) {
		listeners = IListenerList.Utils.list(listenerList, this);
	}

	public void nuke() {
		data.clear();
	}

	public void selected(final String usage) {
		Maps.findOrCreate(data, usage, new Callable<AtomicInteger>() {
			public AtomicInteger call() throws Exception {
				return new AtomicInteger();
			}
		}).incrementAndGet();
		fireListeners();
	}

	public void fireListeners() {
		listeners.fire(new ICallback<IUsageListener>() {
			public void process(IUsageListener t) throws Exception {
				t.usageChanged();
			}
		});
	}

	public Map<String, UsageStats> getStats() {
		return Maps.mapTheMap(data, new IFunction1<AtomicInteger, UsageStats>() {
			public UsageStats apply(AtomicInteger from) throws Exception {
				return new UsageStats(from.get());
			}
		});
	}

	public void setUsage(String path, UsageStats usageStats) {
		synchronized (data) {
			data.put(path, new AtomicInteger(usageStats.count));
		}
	}

	public void addUsageListener(IUsageListener listener) {
		listeners.addListener(listener);

	}

}
