package org.softwareFm.card.api;

import java.util.concurrent.Future;

import org.softwareFm.card.internal.CardDataStoreCache;


public interface ICardDataStore {

	Future<?> processDataFor(String url, ICardDataStoreCallback callback);

	public static class Utils{
		public static ICardDataStore mock(Object...urlsAndMaps){
			return new CardDataStoreMock(urlsAndMaps);
		}
		public static ICardDataStore cache(ICardDataStore raw){
			return new CardDataStoreCache(raw);
		}
	}
	
}
