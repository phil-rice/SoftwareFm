package org.softwareFm.display.data;

import java.util.List;

public interface IDataGetter {
	Object getDataFor(String key);

	ActionData getActionDataFor(List<String> params);
	
	Object getLastRawData();

	void setRawData(Object rawData);
	void clearCache(String url, String entity, String attribute) ;
}
