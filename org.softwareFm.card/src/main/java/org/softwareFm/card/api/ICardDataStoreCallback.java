package org.softwareFm.card.api;

import java.util.Map;

public interface ICardDataStoreCallback {

	void process(String url, Map<String, Object> result);
	
	void noData(String url);

}
