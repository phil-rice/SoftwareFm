package org.softwareFm.card.internal;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardDataStoreCallback;
import org.softwareFm.card.api.IMutableCardDataStore;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.utilities.collections.Sets;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.future.Futures;
import org.softwareFm.utilities.maps.Maps;

public class CardDataStoreCache implements IMutableCardDataStore {

	private final Map<String, Map<String, Object>> cache = Maps.newMap();
	private final Set<String> urlsWithNoData = Sets.newSet();
	private final ICardDataStore raw;

	public CardDataStoreCache(ICardDataStore raw) {
		this.raw = raw;
	}

	@Override
	public <T> Future<T> processDataFor(String url, final ICardDataStoreCallback<T> callback) {
		try {
			if (urlsWithNoData.contains(url)) {
				callback.noData(url);
				return Futures.doneFuture(null);
			}
			Map<String, Object> map = cache.get(url);
			if (map == null)
				return raw.processDataFor(url, new ICardDataStoreCallback<T>() {
					@Override
					public T process(String url, Map<String, Object> map) throws Exception {
						cache.put(url, map);
						T result = callback.process(url, map);
						return result;
					}

					@Override
					public T noData(String url) throws Exception {
						urlsWithNoData.add(url);
						T result = callback.noData(url);
						return result;
					}
				});
			else {
				T result = callback.process(url, map);
				return Futures.doneFuture(result);
			}
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public void put(String url, List<KeyValue> keyValues) {
		if (raw instanceof IMutableCardDataStore) {
			((IMutableCardDataStore) raw).put(url, keyValues);
			cache.remove(url);
			urlsWithNoData.remove(url);
		} else
			throw new IllegalStateException();

	}

}
