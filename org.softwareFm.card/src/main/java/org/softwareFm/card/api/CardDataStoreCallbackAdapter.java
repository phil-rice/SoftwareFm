package org.softwareFm.card.api;

import java.util.Collections;

public abstract class CardDataStoreCallbackAdapter <T>implements ICardDataStoreCallback<T> {

	@Override
	public T noData(String url) throws Exception {
		return process(url, Collections.<String,Object>emptyMap());
	}

}
