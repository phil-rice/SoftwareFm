/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.navigation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.maps.IHasUrlCache;
import org.softwareFm.crowdsource.utilities.maps.UrlCache;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;

public class CachedRepoNavigation implements IRepoNavigation, IHasUrlCache {

	private final IRepoNavigation delegate;
	private final UrlCache<Map<String, List<String>>> cache = new UrlCache<Map<String, List<String>>>();
	private long lastClearedTime;
	private final long cacheTimeOut;
	private final Callable<Long> timeGetter;
	// note that there are now two locks involved: the url cache and this one. I don't think this will ever cause a deadlock, but think about it when changing this code
	private final Object lock = new Object();

	public CachedRepoNavigation(IRepoNavigation delegate, long cacheTimeout, Callable<Long> timeGetter) {
		this.delegate = delegate;
		this.cacheTimeOut = cacheTimeout;
		this.timeGetter = timeGetter;
		this.lastClearedTime = Callables.call(timeGetter);
	}

	@Override
	public ITransaction<Map<String, List<String>>> navigationData(final String url, final ICallback<Map<String, List<String>>> callback) {
		long now = Callables.call(timeGetter);
		if (now >= lastClearedTime + cacheTimeOut) {
			synchronized (lock) {
				if (now > lastClearedTime) {
					lastClearedTime = now;
					cache.clear();
				}
			}
		}
		if (cache.containsKey(url)) {
			Map<String, List<String>> result = cache.get(url);
			ICallback.Utils.call(callback, result);
			return ITransaction.Utils.<Map<String, List<String>>> doneTransaction(result);
		}
		return delegate.navigationData(url, new ICallback<Map<String, List<String>>>() {
			@Override
			public void process(Map<String, List<String>> result) throws Exception {
				cache.put(url, result);
				ICallback.Utils.call(callback, result);
			}
		});
	}

	@Override
	public void clearCaches() {
		synchronized (lock) {
			cache.clear();
		}
	}

	@Override
	public void clearCache(String url) {
		synchronized (lock) {
			cache.clear(url);
		}
	}

}