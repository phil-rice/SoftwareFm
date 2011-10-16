package org.softwareFm.card.internal;

import java.util.List;
import java.util.Map;

import org.softwareFm.card.api.ICardDataStoreCallback;
import org.softwareFm.utilities.collections.Lists;

public class CacheDataStoreCallbackMock implements ICardDataStoreCallback {

	public final List<String> urls = Lists.newList();
	public final List<String> noDataUrls = Lists.newList();
	public final List<Map<String, Object>> results = Lists.newList();

	@Override
	public void process(String url, Map<String, Object> result) {
		this.urls.add(url);
		this.results.add(result);
	}

	@Override
	public void noData(String url) {
		noDataUrls.add(url);
	}

}
