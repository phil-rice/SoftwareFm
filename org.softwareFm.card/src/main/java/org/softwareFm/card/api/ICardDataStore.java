package org.softwareFm.card.api;

import java.util.concurrent.Future;

import org.softwareFm.card.internal.CardDataStoreCache;


public interface ICardDataStore {

	
	<T>Future<T> processDataFor(String url, ICardDataStoreCallback <T>callback);

	public static class Utils{
		public static ICardDataStore mock(Object...urlsAndMaps){
			return new CardDataStoreMock(urlsAndMaps);
		}
		public static ICardDataStore cache(ICardDataStore raw){
			return new CardDataStoreCache(raw);
		}
	}
	
}
