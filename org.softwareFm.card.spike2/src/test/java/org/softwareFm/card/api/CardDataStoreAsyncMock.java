package org.softwareFm.card.api;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.constants.UtilityConstants;
import org.softwareFm.utilities.future.Futures;
import org.softwareFm.utilities.maps.Maps;

public class CardDataStoreAsyncMock implements IMutableCardDataStore {

	private final Map<String, Map<String, Object>> map;
	public final Map<String, Integer> counts = Maps.newMap();

	public CardDataStoreAsyncMock(Object... urlsAndMaps) {
		map = Maps.makeMap(urlsAndMaps);
	}

	@Override
	public Future<Map<String,Object>> processDataFor(final String url, final ICardDataStoreCallback callback) {
		Maps.add(counts, url, 1);
		final Map<String, Object> result = map.get(url);
		if (result == null)
			throw new NullPointerException(MessageFormat.format(UtilityConstants.mapDoesntHaveKey, url, Lists.sort(map.keySet()), map));
		return Futures.gatedMock(new ICallback<Map<String,Object>>(){
			@Override
			public void process(Map<String, Object> t) throws Exception {
				callback.process(url, result);
			}}, result);
	}

	@Override
	public void put(String url, List<KeyValue> keyValues) {
		Map<String, Object> contents = Maps.findOrCreate(map, url, new Callable<Map<String, Object>>() {
			@Override
			public Map<String, Object> call() throws Exception {
				return Maps.newMap();
			}
		});
		for (KeyValue keyValue : keyValues)
			contents.put(keyValue.key, keyValue.value);
	}

}
