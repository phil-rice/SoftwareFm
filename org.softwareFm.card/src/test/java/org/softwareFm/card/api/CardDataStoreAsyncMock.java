package org.softwareFm.card.api;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.softwareFm.card.internal.details.IAfterEditCallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.constants.UtilityConstants;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.future.Futures;
import org.softwareFm.utilities.maps.Maps;

public class CardDataStoreAsyncMock implements IMutableCardDataStore {

	private final Map<String, Map<String, Object>> map;
	public final Map<String, Integer> counts = Maps.newMap();
	public final Map<String,List<Map<String,Object>>> rememberedPuts = Maps.newMap();

	public CardDataStoreAsyncMock(Object... urlsAndMaps) {
		map = Maps.makeMap(urlsAndMaps);
	}

	@Override
	public <T> Future<T> processDataFor(final String url, final ICardDataStoreCallback<T> callback) {
		try {
			Maps.add(counts, url, 1);
			final Map<String, Object> result = map.get(url);
			if (result == null)
				throw new NullPointerException(MessageFormat.format(UtilityConstants.mapDoesntHaveKey, url, Lists.sort(map.keySet()), map));
			return Futures.gatedMock(new IFunction1<Map<String, Object>, T>() {
				@Override
				public T apply(Map<String, Object> from) throws Exception {
					T process = callback.process(url, from);
					System.out.println("CardAysnc: " + url + "=>" + from + "=> " + process);
					return process;
				}
			}, result);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public void put(String url, Map<String, Object> map, IAfterEditCallback afterEdit) {
		Maps.addToList(rememberedPuts, url, map);
		afterEdit.afterEdit(url);
	}

}
