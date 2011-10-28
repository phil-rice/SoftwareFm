package org.softwareFm.card.api;

import java.util.Map;
import java.util.concurrent.Future;

import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.future.Futures;
import org.softwareFm.utilities.maps.Maps;

public class CardDataStoreMock implements IMutableCardDataStore {

	private final Map<String, Map<String, Object>> map;
	public final Map<String, Integer> counts = Maps.newMap();

	public CardDataStoreMock(Object... urlsAndMaps) {
		map = Maps.makeMap(urlsAndMaps);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Future<Map<String, Object>> processDataFor(String url, ICardDataStoreCallback callback) {
		try {
			Maps.add(counts, url, 1);
			Map<String, Object> result = map.get(url);

			if (result == null)
				callback.noData(url);
			else
				callback.process(url, result);
			return Futures.doneFuture(result);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}


	@Override
	public void put(String url, Map<String, Object> map) {
		throw new UnsupportedOperationException();
	}

}
