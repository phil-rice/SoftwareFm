/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.dataStore;

import java.util.Map;

import org.softwareFm.utilities.exceptions.WrappedException;

/** Data has been found for the card */
public interface ICardDataStoreCallback<T> {

	T process(String url, Map<String, Object> result) throws Exception;

	T noData(String url) throws Exception;

	public static class Utils {
		public static <T> T process(ICardDataStoreCallback<T> callback, String url, Map<String, Object> result) {
			try {
				return callback.process(url, result);
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
		}

		public static <T> T noData(ICardDataStoreCallback<T> callback, String url) {
			try {
				return callback.noData(url);
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
		}
	}

}