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
	public Object getLastRawData(String entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRawData(String entity, Object rawData) {
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
