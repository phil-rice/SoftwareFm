/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.dataStore;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.UtilityConstants;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.transaction.GatedTransaction;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.swt.dataStore.IAfterEditCallback;
import org.softwareFm.swt.dataStore.ICardDataStoreCallback;
import org.softwareFm.swt.dataStore.IMutableCardDataStore;

public class CardDataStoreAsyncMock implements IMutableCardDataStore {

	private final Map<String, Map<String, Object>> map;
	public final Map<String, Integer> counts = Maps.newMap();
	public final Map<String, List<Map<String, Object>>> rememberedPuts = Maps.newMap();
	private final List<GatedTransaction<?>> transactions = Lists.newList();

	public CardDataStoreAsyncMock(Object... urlsAndMaps) {
		map = Maps.makeMap(urlsAndMaps);
	}

	@Override
	public void clearCaches() {
	}

	@Override
	public <T> ITransaction<T> processDataFor(final String url, final ICardDataStoreCallback<T> callback) {
		try {
			Maps.add(counts, url, 1);
			final Map<String, Object> result = map.get(url);
			if (result == null)
				throw new NullPointerException(MessageFormat.format(UtilityConstants.mapDoesntHaveKey, url, Lists.sort(map.keySet()), map));
			GatedTransaction<T> transaction = ITransaction.Utils.gateTransaction(new IFunction1<Map<String, Object>, T>() {
				@Override
				public T apply(Map<String, Object> from) throws Exception {
					T process = callback.process(url, from);
					System.out.println("CardAysnc: " + url + "=>" + from + "=> " + process);
					return process;
				}
			}, result);
			transactions.add(transaction);
			return transaction;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public ITransaction<?> put(String url, Map<String, Object> map, IAfterEditCallback afterEdit) {
		Maps.addToList(rememberedPuts, url, map);
		afterEdit.afterEdit(url);
		return ITransaction.Utils.doneTransaction(null);
	}

	public void kickAllFutures() {
		for (GatedTransaction<?> f : transactions)
			f.kick();
	}

	@Override
	public void clearCache(String url) {
	}

	@Override
	public ITransaction<?> makeRepo(String url, IAfterEditCallback callback) {
		throw new UnsupportedOperationException();
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