package org.softwareFm.card.dataStore;


public interface IUpdateDataStore {
	void updateDataStore(IMutableCardDataStore store, String url, String key, Object value);
}
