/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.dataStore;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import junit.framework.Assert;

import org.softwareFm.utilities.collections.Sets;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.future.Futures;
import org.softwareFm.utilities.maps.Maps;

public class CardDataStoreMock implements IMutableCardDataStore {

	private final Map<String, Map<String, Object>> map;
	public final Map<String, Integer> counts = Maps.newMap();
	public final Map<String, Map<String, Object>> updateMap = Maps.newMap();
	public final Set<String> repos = Sets.newSet();

	public CardDataStoreMock(Object... urlsAndMaps) {
		map = Maps.makeMap(urlsAndMaps);
	}

	@Override
	public void clearCaches() {
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
	public Future<?> put(String url, Map<String, Object> map, IAfterEditCallback callback) {
		if (updateMap.containsKey(url))
			Assert.fail(url + " <- " + map);
		updateMap.put(url, map);
		callback.afterEdit(url);
		return Futures.doneFuture(null);
	}

	@Override
	public void clearCache(String url) {
	}

	@Override
	public Future<?> makeRepo(String url, IAfterEditCallback callback) {
		repos.add(url);
		callback.afterEdit(url);
		return Futures.doneFuture(null);
	}


	@Override
	public void delete(String url, IAfterEditCallback callback) {
		throw new UnsupportedOperationException();
	}
	@Override
	public void refresh(String url) {
		throw new UnsupportedOperationException();
	}

}