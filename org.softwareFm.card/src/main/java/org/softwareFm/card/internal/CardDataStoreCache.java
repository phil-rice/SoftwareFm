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
	public Future<?> processDataFor(String url, final ICardDataStoreCallback callback) {
		if (urlsWithNoData.contains(url)) {
			callback.noData(url);
			return Futures.doneFuture(null);
		}
		Map<String, Object> result = cache.get(url);
		if (result == null)
			return raw.processDataFor(url, new ICardDataStoreCallback() {
				@Override
				public void process(String url, Map<String, Object> result) {
					cache.put(url, result);
					callback.process(url, result);
				}

				@Override
				public void noData(String url) {
					urlsWithNoData.add(url);
					callback.noData(url);
				}
			});
		else {
			callback.process(url, result);
			return Futures.doneFuture(result);
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
