package org.softwareFm.card.internal.details;

import org.softwareFm.card.api.IMutableCardDataStore;

public interface IUpdateDataStore {
	void updateDataStore(IMutableCardDataStore store, String url, String key, Object value);
}
