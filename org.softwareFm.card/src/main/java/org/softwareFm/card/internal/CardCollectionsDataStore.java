package org.softwareFm.card.internal;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardAndCollectionsDataStore;
import org.softwareFm.card.api.ICardDataStoreCallback;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.ICardHolder;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.future.Futures;
import org.softwareFm.utilities.future.GatedFuture;

/** Note that the callback is called early, but the card may get prodded with valueHasChanged, and the future isn't done until all values have been changed */
public class CardCollectionsDataStore implements ICardAndCollectionsDataStore {


	@Override
	public CardAndCollectionsStatus processDataFor(final ICardHolder cardHolder, final CardConfig cardConfig, final String url) {
		final GatedFuture<Void> future = Futures.gatedFuture();
		final AtomicInteger count = new AtomicInteger(1);
		final List<Future<KeyValue>> keyValueFutures = new CopyOnWriteArrayList<Future<KeyValue>>();
		Future<ICard> cardFuture = ICardFactory.Utils.makeCard(cardHolder, cardConfig, url, new ICallback<ICard>() {
			@Override
			public void process(final ICard card) throws Exception {
				cardHolder.setCard(card);
				for (final KeyValue keyValue : card.data()) {
					String followOnUrlFragment = findFollowOnUrlFragment(keyValue);
					if (followOnUrlFragment!= null) {
						count.incrementAndGet();
						Future<KeyValue> future = cardConfig.cardDataStore.processDataFor(url + "/" + followOnUrlFragment, new ICardDataStoreCallback<KeyValue>() {
							@Override
							public KeyValue process(String url, Map<String, Object> result) throws Exception {
								card.valueChanged(keyValue, result);
								finish(card);
								return keyValue;
							}

							@Override
							public KeyValue noData(String url) throws Exception {
								finish(card);
								return null;
							}
						});
						keyValueFutures.add(future);
					}

				}
				finish(card);
			}

			private void finish(ICard card) {
				if (count.decrementAndGet() == 0)
					future.done(null);
			}
		});
		final CardAndCollectionsStatus status = new CardAndCollectionsStatus(future, cardFuture, keyValueFutures, count);
		return status;
	}

	protected String findFollowOnUrlFragment(KeyValue keyValue) {
		return null;
	}

}
