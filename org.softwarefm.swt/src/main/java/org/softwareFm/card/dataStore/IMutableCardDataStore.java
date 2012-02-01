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
import java.util.concurrent.Future;

import org.softwareFm.card.card.RightClickCategoryResult;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.common.exceptions.WrappedException;
import org.softwareFm.common.maps.Maps;

/** This is a card data store that can be changed. */
public interface IMutableCardDataStore extends ICardDataStore {

	void refresh(String url);
	
	void clearCache(String url);

	Future<?> put(String url, Map<String, Object> map, IAfterEditCallback callback);

	void delete(String url,IAfterEditCallback callback);

	Future<?> makeRepo(String url, IAfterEditCallback callback);

	public static class Utils {

		public static Future<Void> addCollectionItem(final IMutableCardDataStore store, RightClickCategoryResult result, final String itemUrlFragment, final Map<String, Object> map, final IAfterEditCallback callback) {
			String collectionUrl = result.collectionUrl();
			switch (result.itemType) {
			case COLLECTION_NOT_CREATED_YET:
				return addCollectionItemToCollection(store, collectionUrl, result.collectionName, itemUrlFragment, map, callback);
			case IS_COLLECTION:
				return addCollectionItemToCollection(store, collectionUrl, result.collectionName, itemUrlFragment, map, callback);
			case ROOT_COLLECTION:
				return addCollectionItemToCollection(store, collectionUrl, result.collectionName, itemUrlFragment, map, callback);
			default:
				throw new IllegalStateException(result.itemType.toString());
			}

		}

		public static Future<Void> addCollectionItemToBase(final IMutableCardDataStore store, String baseUrl, String collectionName, final String itemUrlFragment, final Map<String, Object> map, final IAfterEditCallback callback) {
			final String collectionUrl = baseUrl + "/" + collectionName;
			return addCollectionItemToCollection(store, collectionUrl, collectionName, itemUrlFragment, map, callback);
		}

		public static Future<Void> addCollectionItemToCollection(final IMutableCardDataStore store, final String collectionUrl, String collectionName, final String itemUrlFragment, final Map<String, Object> map, final IAfterEditCallback callback) {
			store.clearCache(collectionUrl);
			return store.processDataFor(collectionUrl, new ICardDataStoreCallback<Void>() {
				@Override
				public Void process(String url, Map<String, Object> data) throws Exception {// the collection exists
					String itemUrl = collectionUrl + "/" + itemUrlFragment;
					store.put(itemUrl, map, callback);
					return null;
				}

				@Override
				public Void noData(final String url) throws Exception {
					final Map<String, Object> newData = Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.collection);
					store.put(url, newData, new IAfterEditCallback() {
						@Override
						public void afterEdit(String url) {
							try {
								process(url, newData);
							} catch (Exception e) {
								throw WrappedException.wrap(e);
							}
						}
					});
					return null;
				}
			});
		}
	}

}