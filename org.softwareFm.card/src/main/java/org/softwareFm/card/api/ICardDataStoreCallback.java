package org.softwareFm.card.api;

import java.util.Map;

public interface ICardDataStoreCallback<T> {

	T process(String url, Map<String, Object> result) throws Exception;

	T noData(String url) throws Exception;

}
