package org.softwareFm.crowdsource.navigation;

import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.maps.UrlCache;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;

public class CachedServerRepoNavigation implements IRepoNavigation{

	private final IRepoNavigation delegate;
	private final UrlCache<Map<String,List<String>>> cache = new UrlCache<Map<String,List<String>>>();

	public CachedServerRepoNavigation(IRepoNavigation delegate) {
		this.delegate = delegate;
	}

	@Override
	public ITransaction<Map<String, List<String>>> navigationData(final String url, final ICallback<Map<String, List<String>>> callback) {
		if (cache.containsKey(url)) {
			Map<String, List<String>> result = cache.get(url);
			ICallback.Utils.call(callback, result);
			return ITransaction.Utils.<Map<String,List<String>>>doneTransaction(result);
		}
		return delegate.navigationData(url, new ICallback<Map<String,List<String>>>(){
			@Override
			public void process(Map<String, List<String>> result) throws Exception {
				cache.put(url, result);
				ICallback.Utils.call(callback, result);
			}});
	}

}
