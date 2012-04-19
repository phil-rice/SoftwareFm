/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.dataStore;

import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.softwareFm.crowdsource.utilities.collections.Sets;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.swt.dataStore.IAfterEditCallback;
import org.softwareFm.swt.dataStore.ICardDataStoreCallback;
import org.softwareFm.swt.dataStore.IMutableCardDataStore;

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
	public ITransaction<?> processDataFor(String url, ICardDataStoreCallback callback) {
		try {
			Maps.add(counts, url, 1);
			Map<String, Object> result = map.get(url);

			if (result == null)
				callback.noData(url);
			else
				callback.process(url, result);
			return ITransaction.Utils.doneTransaction(result);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public ITransaction<?> put(String url, Map<String, Object> map, IAfterEditCallback callback) {
		if (updateMap.containsKey(url))
			Assert.fail(url + " <- " + map);
		updateMap.put(url, map);
		callback.afterEdit(url);
		return ITransaction.Utils.doneTransaction(null);
	}

	@Override
	public void clearCache(String url) {
	}

	@Override
	public ITransaction<?> makeRepo(String url, IAfterEditCallback callback) {
		repos.add(url);
		callback.afterEdit(url);
		return ITransaction.Utils.doneTransaction(null);
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