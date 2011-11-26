/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.dataStore;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.constants.UtilityConstants;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.future.Futures;
import org.softwareFm.utilities.future.GatedMockFuture;
import org.softwareFm.utilities.maps.Maps;

public class CardDataStoreAsyncMock implements IMutableCardDataStore {

	private final Map<String, Map<String, Object>> map;
	public final Map<String, Integer> counts = Maps.newMap();
	public final Map<String, List<Map<String, Object>>> rememberedPuts = Maps.newMap();
	private final List<GatedMockFuture<?, ?>> futures = Lists.newList();

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
			GatedMockFuture<Map<String, Object>, T> future = Futures.gatedMock(new IFunction1<Map<String, Object>, T>() {
				@Override
				public T apply(Map<String, Object> from) throws Exception {
					T process = callback.process(url, from);
					System.out.println("CardAysnc: " + url + "=>" + from + "=> " + process);
					return process;
				}
			}, result);
			futures.add(future);
			return future;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public Future<?> put(String url, Map<String, Object> map, IAfterEditCallback afterEdit) {
		Maps.addToList(rememberedPuts, url, map);
		afterEdit.afterEdit(url);
		return Futures.doneFuture(null);
	}

	public void kickAllFutures() {
		for (GatedMockFuture<?, ?> f : futures)
			f.kick();
	}

}