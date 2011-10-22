package org.softwareFm.card.api;

import java.util.concurrent.Future;

import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.internal.CardDataStoreCache;
import org.softwareFm.card.internal.CardDataStoreForRepository;
import org.softwareFm.repositoryFacard.IRepositoryFacard;

public interface ICardDataStore {

	<T> Future<T> processDataFor(String url, ICardDataStoreCallback<T> callback);

	public static class Utils {
		public static ICardDataStore mock(Object... urlsAndMaps) {
			return new CardDataStoreMock(urlsAndMaps);
		}

		public static ICardDataStore cache(ICardDataStore raw) {
			return new CardDataStoreCache(raw);
		}

		/** The control is used to ensure that call backs are in the correct thread, and everything ceases to work if the control is disposed */
		public static ICardDataStore repositoryCardDataStore(Control from, IRepositoryFacard repository){
			return new CardDataStoreForRepository(from, repository);
		}
	}

}
