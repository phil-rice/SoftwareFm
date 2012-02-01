/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.dataStore;

import java.util.List;
import java.util.Map;

import org.softwareFm.common.collections.Lists;

public class CacheDataStoreCallbackMock<T> implements ICardDataStoreCallback<T> {

	public final List<String> urls = Lists.newList();
	public final List<String> noDataUrls = Lists.newList();
	public final List<Map<String, Object>> results = Lists.newList();

	@Override
	public T process(String url, Map<String, Object> result) {
		this.urls.add(url);
		this.results.add(result);
		return null;
	}

	@Override
	public T noData(String url) {
		noDataUrls.add(url);
		return null;
	}

}