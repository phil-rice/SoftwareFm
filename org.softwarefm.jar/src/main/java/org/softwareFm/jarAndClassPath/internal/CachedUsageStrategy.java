/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.internal;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.softwareFm.crowdsource.api.UserData;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.utilities.collections.Sets;
import org.softwareFm.crowdsource.utilities.future.Futures;
import org.softwareFm.crowdsource.utilities.maps.IHasCache;
import org.softwareFm.jarAndClassPath.api.IUsageStrategy;
import org.softwareFm.jarAndClassPath.api.IUserDataListener;
import org.softwareFm.jarAndClassPath.api.IUserDataManager;

public class CachedUsageStrategy implements IUsageStrategy, IHasCache {

	private final IUsageStrategy delegate;
	private final long period;
	private long lastUpdate = System.currentTimeMillis();
	private final Set<String> cached = Collections.synchronizedSet(Sets.<String> newSet());
	private final IHasCache cachesToClearWhenUsageOccures;

	public CachedUsageStrategy(IUsageStrategy delegate, long period, IHasCache cachesToClearWhenUsageOccures, IUserDataManager userDataManager) {
		this.delegate = delegate;
		this.period = period;
		this.cachesToClearWhenUsageOccures = cachesToClearWhenUsageOccures;
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
		cachesToClearWhenUsageOccures.clearCaches();
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