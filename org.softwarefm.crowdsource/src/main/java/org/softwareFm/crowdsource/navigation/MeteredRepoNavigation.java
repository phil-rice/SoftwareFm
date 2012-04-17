package org.softwareFm.crowdsource.navigation;

import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;

public class MeteredRepoNavigation implements IRepoNavigation {

	private final Map<String, Integer> urlToCount = Maps.newMap();
	private final IRepoNavigation delegate;

	public MeteredRepoNavigation(IRepoNavigation repoNavigation) {
		delegate = repoNavigation;
	}

	@Override
	public ITransaction<Map<String, List<String>>> navigationData(String url, ICallback<Map<String, List<String>>> callback) {
		Maps.add(urlToCount, url, 1);
		return delegate.navigationData(url, callback);
	}
	
	public int countFor(String url){
		return Maps.intFor(urlToCount, url);
	}

}
