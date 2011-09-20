package org.softwareFm.display.data;

import org.softwareFm.utilities.resources.IResourceGetter;

public class DataGetter implements IDataGetter {

	private final IDataGetter nestedDataGetter;
	private final IResourceGetter resourceGetter;

	public DataGetter(IDataGetter nestedDataGetter, IResourceGetter resourceGetter) {
		this.nestedDataGetter = nestedDataGetter;
		this.resourceGetter = resourceGetter;
	}

	@Override
	public Object getDataFor(String key) {
		if (key.startsWith("data.")) {
			String substring = key.substring(5);
			return nestedDataGetter.getDataFor(substring);
		} else
			return resourceGetter.getStringOrNull(key);
	}

	@Override
	public Object getLastRawData() {
		return nestedDataGetter.getLastRawData();
	}

	public void setRawData(Object rawData) {
		nestedDataGetter.setRawData( rawData);
	}

}
