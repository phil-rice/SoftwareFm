package org.softwareFm.card.api;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.internal.CardFactory;
import org.softwareFm.utilities.callbacks.ICallback;

public interface ICardFactory extends ILineFactory {

	ICard makeCard(Composite parent, ICardDataStore cardDataStore, String url, ICallback<ICard> callbackWhenPopulated);

	
	
	CardConfig getCardConfig();

	public static class Utils {
		public static ICardFactory cardFactory() {
			return new CardFactory();
		};
		public static ICardFactoryWithAggregateAndSort cardFactoryWithAggregateAndSort() {
			return new CardFactory();
		};

	}

}
