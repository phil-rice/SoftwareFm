package org.softwarefm.shared.usage.internal;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwarefm.shared.usage.IUsage;
import org.softwarefm.shared.usage.IUsageListener;
import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.shared.usage.UsageStatData;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.events.IListenerList;
import org.softwarefm.utilities.events.IMultipleListenerList;
import org.softwarefm.utilities.functions.IFunction1;
import org.softwarefm.utilities.maps.Maps;

public class Usage implements IUsage {

	private final Map<String, AtomicInteger> data = Maps.makeMap();
	private final IListenerList<IUsageListener> listeners;

	public Usage(IMultipleListenerList listenerList) {
		listeners = IListenerList.Utils.list(listenerList, IUsageListener.class, this);
	}

	public void nuke() {
		data.clear();
		fireListeners();
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

	public IUsageStats getStats() {
		return IUsageStats.Utils.fromMap(Maps.mapTheMap(data, new IFunction1<AtomicInteger, UsageStatData>() {
			public UsageStatData apply(AtomicInteger from) throws Exception {
				return new UsageStatData(from.get());
			}
		}));
	}

	public void setUsage(String path, UsageStatData usageStatData) {
		synchronized (data) {
			data.put(path, new AtomicInteger(usageStatData.count));
		}
	}

	public void addUsageListener(IUsageListener listener) {
		listeners.addListener(listener);

	}

}
