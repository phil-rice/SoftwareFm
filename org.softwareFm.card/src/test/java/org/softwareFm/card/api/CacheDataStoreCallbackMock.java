package org.softwareFm.card.api;

import java.util.List;
import java.util.Map;

import org.softwareFm.utilities.collections.Lists;

public class CacheDataStoreCallbackMock<T> implements ICardDataStoreCallback<T> {

	public final List<String> urls = Lists.newList();
	public final List<String> noDataUrls = Lists.newList();
	public final List<Map<String, Object>> results = Lists.newList();

	@Override
	public T process(String url, Map<String, Object> result) {
		this.urls.add(url);
		this.results.add(result);
		return null;
	}

	@Override
	public T noData(String url) {
		noDataUrls.add(url);
		return null;
	}

}
