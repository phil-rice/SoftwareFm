package org.softwareFm.display.data;

import java.util.List;
import java.util.Map;

public interface IDataGetter {
	Object getDataFor(String key);

	ActionData getActionDataFor(List<String> params);

	Map<String, Object> getLastRawData(String entity);

	void setRawData(String entity, Map<String, Object> rawData);

	void clearCache(String url, String entity, String attribute);
}
