package org.softwareFm.card.api;

import java.util.Map;

import org.softwareFm.card.internal.details.IAfterEditCallback;

public interface IMutableCardDataStore extends ICardDataStore {

	void put(String url, Map<String, Object> map, IAfterEditCallback callback);

}
