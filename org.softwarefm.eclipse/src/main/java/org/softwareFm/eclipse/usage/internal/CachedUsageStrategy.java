package org.softwareFm.eclipse.usage.internal;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.common.collections.Sets;
import org.softwareFm.common.future.Futures;
import org.softwareFm.common.maps.IHasCache;
import org.softwareFm.eclipse.usage.IUsageStrategy;
import org.softwareFm.swt.explorer.IUserDataListener;
import org.softwareFm.swt.explorer.IUserDataManager;
import org.softwareFm.swt.explorer.internal.UserData;

public class CachedUsageStrategy implements IUsageStrategy, IHasCache {

	private final IUsageStrategy delegate;
	private final long period;
	private long lastUpdate = System.currentTimeMillis();
	private final Set<String> cached = Collections.synchronizedSet(Sets.<String> newSet());

	public CachedUsageStrategy(IUsageStrategy delegate, long period, IUserDataManager userDataManager) {
		this.delegate = delegate;
		this.period = period;
		userDataManager.addUserDataListener(new IUserDataListener() {
			@Override
			public void userDataChanged(Object source, UserData userData) {
				clearCaches();
			}
		});
	}

	@Override
	public Future<?> using(String softwareFmId, String digest, IResponseCallback callback) {
		if (System.currentTimeMillis() > lastUpdate + period)
			clearCaches();
		if (!cached.add(digest))
			return Futures.doneFuture(null);
		return delegate.using(softwareFmId, digest, callback);
	}

	@Override
	public Map<String, Object> myProjectData(String softwareFmId, String crypto) {
		return delegate.myProjectData(softwareFmId, crypto);
	}

	@Override
	public void clearCaches() {
		cached.clear();
		lastUpdate = System.currentTimeMillis();
	}

}
