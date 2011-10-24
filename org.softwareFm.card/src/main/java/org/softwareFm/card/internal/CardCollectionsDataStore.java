package org.softwareFm.card.internal;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.card.api.CardAndCollectionDataStoreVisitorMock;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardAndCollectionsDataStore;
import org.softwareFm.card.api.ICardDataStoreCallback;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.ICardHolder;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.future.Futures;
import org.softwareFm.utilities.future.GatedFuture;

/** Note that the callback is called early, but the card may get prodded with valueHasChanged, and the future isn't done until all values have been changed */
public class CardCollectionsDataStore implements ICardAndCollectionsDataStore {

	@Override
	public CardAndCollectionsStatus processDataFor(final ICardHolder cardHolder, final CardConfig cardConfig, final String url, final CardAndCollectionDataStoreVisitorMock visitor) {
		final GatedFuture<Void> future = Futures.gatedFuture();
		final AtomicInteger count = new AtomicInteger(1);
		final List<Future<KeyValue>> keyValueFutures = new CopyOnWriteArrayList<Future<KeyValue>>();
		visitor.initialUrl(cardHolder, cardConfig, url);
		Future<ICard> cardFuture = ICardFactory.Utils.makeCard(cardHolder, cardConfig, url, new ICallback<ICard>() {
			@Override
			public void process(final ICard card) throws Exception {
				visitor.initialCard(cardHolder, cardConfig, url, card);
				cardHolder.setCard(card);
				for (final KeyValue keyValue : card.data()) {
					final String followOnUrlFragment = findFollowOnUrlFragment(keyValue);
					if (followOnUrlFragment != null) {
						count.incrementAndGet();
						visitor.requestingFollowup(cardHolder, url, card, followOnUrlFragment);
						Future<KeyValue> future = cardConfig.cardDataStore.processDataFor(url + "/" + followOnUrlFragment, new ICardDataStoreCallback<KeyValue>() {
							@Override
							public KeyValue process(String followUpUrl, Map<String, Object> result) throws Exception {
								visitor.followedUp(cardHolder, url, card, followUpUrl, result);
								if (!cardHolder.getControl().isDisposed() && !card.getControl().isDisposed()) {
									List<KeyValue> list = Lists.newList();
									// the card is expecting a list of key values for each child (probably each child of a type...but lets not go there yet)
									for (Entry<String, Object> entry : result.entrySet())
										if (entry.getValue() instanceof Map<?, ?>)
											list.add(new KeyValue(followOnUrlFragment + "/" + entry.getKey(), entry.getValue()));
									card.valueChanged(keyValue, list);
								}
								finish(card);
								return keyValue;
							}

							@Override
							public KeyValue noData(String followUpUrl) throws Exception {
								visitor.noData(cardHolder, url, card, followUpUrl);
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
				if (count.decrementAndGet() == 0) {
					visitor.finished(cardHolder, url, card);
					future.done(null);
				}
			}
		});
		final CardAndCollectionsStatus status = new CardAndCollectionsStatus(future, cardFuture, keyValueFutures, count);
		return status;
	}

	protected String findFollowOnUrlFragment(KeyValue keyValue) {
		return null;
	}

}
