/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.display.data;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.url.IUrlDataCallback;
import org.softwareFm.utilities.url.IUrlToData;

public class UrlToDataMock implements IUrlToData {

	private final Map<String, AtomicInteger> count = Maps.newMap();
	public List<String> entities = Lists.newList();
	public List<String> urls = Lists.newList();
	public List<Map<String, Object>> contexts = Lists.newList();

	private final Map<String, List<Map<String, Object>>> mockData;

	public UrlToDataMock(Object... entitiesAndListOfMaps) {
		this(Maps.<String, List<Map<String, Object>>> makeLinkedMap(entitiesAndListOfMaps));

	}

	public UrlToDataMock(Map<String, List<Map<String, Object>>> mockData) {
		this.mockData = mockData;
	}

	@Override
	public void getData(String entity, String url, Map<String, Object> context, IUrlDataCallback callback) {
		try {
			entities.add(entity);
			urls.add(url);
			contexts.add(context);
			AtomicInteger integer = Maps.findOrCreate(count, entity, new Callable<AtomicInteger>() {
				@Override
				public AtomicInteger call() throws Exception {
					return new AtomicInteger();
				}
			});
			List<Map<String, Object>> list = mockData.get(entity);
			Map<String, Object> data = list.get(integer.getAndIncrement());
			callback.processData(entity, url, context, data);
		} catch (Exception e) {
			throw new RuntimeException("Entity: " + entity + ", Url: " + url, e);
		}
	}
}