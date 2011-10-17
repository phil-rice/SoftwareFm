package org.softwareFm.card.api;

import java.util.List;


public interface IMutableCardDataStore extends ICardDataStore{

	void put(String url, List<KeyValue> keyValues);
	
}
