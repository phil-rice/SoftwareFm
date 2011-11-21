package org.softwareFm.card.dataStore;

import java.util.Collections;
/** An implementation with empty methods of {@link ICardDataStoreCallback}, it treats noData as though an empty map had been returned */

public abstract class CardDataStoreCallbackAdapter <T>implements ICardDataStoreCallback<T> {

	@Override
	public T noData(String url) throws Exception {
		return process(url, Collections.<String,Object>emptyMap());
	}

}
