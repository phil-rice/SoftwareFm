package org.softwareFm.card.api;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.Future;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.internal.CardFactory;
import org.softwareFm.utilities.callbacks.ICallback;

public interface ICardFactory {

	Future<ICard> makeCard(Composite parent, int style, boolean allowSelection, String url, ICallback<ICard> callback);

	ICard makeCard(Composite parent, int style, boolean allowSelection, String url, Map<String, Object> map);

	Comparator<KeyValue> comparator();

	public static class Utils {
		public static ICardFactory cardFactory(ICardDataStore cardDataStore) {
			return new CardFactory(cardDataStore, null);
		};

		public static ICardFactory cardFactory(ICardDataStore cardDataStore, String tag) {
			return new CardFactory(cardDataStore, tag);
		};

		public static ICardFactoryWithAggregateAndSort cardFactoryWithAggregateAndSort(ICardDataStore cardDataStore) {
			return new CardFactory(cardDataStore, null);
		};

	}

}
