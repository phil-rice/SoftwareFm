package org.softwareFm.card.api;

import java.util.Map;

public interface IMutableCardDataStore extends ICardDataStore {

	void put(String url, Map<String, Object> map, Runnable afterEdit);

}
