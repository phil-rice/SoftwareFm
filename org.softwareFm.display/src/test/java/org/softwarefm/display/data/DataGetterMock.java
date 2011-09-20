package org.softwareFm.display.data;

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
	public Object getLastRawData() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRawData(Object rawData) {
		throw new UnsupportedOperationException();
	}

}
