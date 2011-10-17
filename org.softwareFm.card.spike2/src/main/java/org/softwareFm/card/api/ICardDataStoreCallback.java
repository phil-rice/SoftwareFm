package org.softwareFm.card.api;

import java.util.Map;

public interface ICardDataStoreCallback {

	void process(String url, Map<String, Object> result) throws Exception;
	
	void noData(String url) throws Exception;

}
