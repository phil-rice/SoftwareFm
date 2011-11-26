/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.display.data;

import java.util.List;
import java.util.Map;

import org.softwareFm.utilities.maps.Maps;

public class DataGetterMock implements IDataGetter {

	private final Map<String, Object> map;

	public DataGetterMock(Object... namesAndValues) {
		this.map = Maps.<String, Object> makeLinkedMap(namesAndValues);
	}

	@Override
	public Object getDataFor(String key) {
		return map.get(key);
	}

	@Override
	public Map<String, Object> getLastRawData(String entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRawData(String entity, Map<String, Object> rawData) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ActionData getActionDataFor(List<String> params) {
		return new ActionData(Maps.<String,String>newMap(), params	, null);
	}

	@Override
	public void clearCache(String url, String entity, String attribute) {
		throw new UnsupportedOperationException();
	}

}