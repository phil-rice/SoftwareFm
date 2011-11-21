package org.softwareFm.card.dataStore;

import java.util.Map;

/** Data has been found for the card */
public interface ICardDataStoreCallback<T> {

	T process(String url, Map<String, Object> result) throws Exception;

	T noData(String url) throws Exception;

}
