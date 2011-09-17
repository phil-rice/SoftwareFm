package org.softwarefm.display.data;

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

}
