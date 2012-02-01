/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.dataStore;

import java.util.concurrent.Future;

import org.eclipse.swt.widgets.Control;
import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.services.IServiceExecutor;
import org.softwareFm.swt.card.dataStore.CardDataStoreMock;
import org.softwareFm.swt.dataStore.internal.CardDataStoreForRepository;

/** Go get data for a url, no follow ups */
public interface ICardDataStore {

	/** Go get data for a url, no follow ups */
	<T> Future<T> processDataFor(String url, ICardDataStoreCallback<T> callback);

	void clearCaches();

	public static class Utils {
		public static IMutableCardDataStore mock(Object... urlsAndMaps) {
			return new CardDataStoreMock(urlsAndMaps);
		}

		/** The control is used to ensure that call backs are in the correct thread, and everything ceases to work if the control is disposed */
		public static IMutableCardDataStore repositoryCardDataStore(Control from, IServiceExecutor serviceExecutor, IGitLocal gitLocal) {
			return new CardDataStoreForRepository(from, serviceExecutor, gitLocal);
		}
	}

}