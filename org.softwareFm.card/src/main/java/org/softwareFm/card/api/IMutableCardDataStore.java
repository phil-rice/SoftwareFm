package org.softwareFm.card.api;

import java.util.Map;
import java.util.concurrent.Future;

/** This is a card data store that can be changed. */
public interface IMutableCardDataStore extends ICardDataStore {

	Future<?> put(String url, Map<String, Object> map, IAfterEditCallback callback);

}
