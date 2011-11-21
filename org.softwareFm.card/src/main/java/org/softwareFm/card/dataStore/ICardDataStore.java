package org.softwareFm.card.dataStore;

import java.util.concurrent.Future;

import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.CardDataStoreMock;
import org.softwareFm.card.dataStore.internal.CardDataStoreForRepository;
import org.softwareFm.repositoryFacard.IRepositoryFacard;

/** Go get data for a url, no follow ups */
public interface ICardDataStore {

	/** Go get data for a url, no follow ups */
	<T> Future<T> processDataFor(String url, ICardDataStoreCallback<T> callback);

	public static class Utils {
		public static ICardDataStore mock(Object... urlsAndMaps) {
			return new CardDataStoreMock(urlsAndMaps);
		}

		/** The control is used to ensure that call backs are in the correct thread, and everything ceases to work if the control is disposed */
		public static ICardDataStore repositoryCardDataStore(Control from, IRepositoryFacard repository) {
			return new CardDataStoreForRepository(from, repository);
		}
	}

}
