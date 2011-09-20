package org.softwareFm.display.data;

public interface IDataGetter {
	Object getDataFor(String key);

	Object getLastRawData();

	void setRawData(Object rawData);
}