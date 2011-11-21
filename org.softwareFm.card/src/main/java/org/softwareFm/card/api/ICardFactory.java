package org.softwareFm.card.api;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import org.softwareFm.card.internal.CardFactory;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.maps.Maps;


public interface ICardFactory {

	/** Make a component to display the data */
	ICard makeCard(ICardHolder cardHolder, CardConfig cardConfig, String url, Map<String, Object> map);

	public static class Utils {
		public static Future<ICard> makeCard(final ICardHolder cardHolder, final CardConfig cardConfig, String url, final ICallback<ICard> callback) {
			Future<ICard> cardFuture = cardConfig.cardDataStore.processDataFor(url, new ICardDataStoreCallback<ICard>() {
				@Override
				public ICard process(final String url, final Map<String, Object> result) throws Exception {
					final AtomicReference<ICard> ref = new AtomicReference<ICard>();
					Swts.syncExec(cardHolder, new Runnable() {
						@Override
						public void run() {
							try {
								ICard card = cardConfig.cardFactory.makeCard(cardHolder, cardConfig, url, result);
								callback.process(card);
								ref.set(card);
							} catch (Exception e) {
								throw WrappedException.wrap(e);
							}
						}
					});
					return ref.get();
				}

				@Override
				public ICard noData(String url) throws Exception {
					return process(url, Maps.<String, Object> newMap());
				}
			});
			return cardFuture;

		}

		public static ICardFactory cardFactory() {
			return new CardFactory();
		};

		public static CardFactoryMock mockCardFactory() {
			return new CardFactoryMock();
		};

	}

}
