package org.softwareFm.card.dataStore.internal;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.ICardHolder;
import org.softwareFm.card.api.LineItem;
import org.softwareFm.card.dataStore.CardAndCollectionsStatus;
import org.softwareFm.card.dataStore.ICardAndCollectionDataStoreVisitor;
import org.softwareFm.card.dataStore.ICardAndCollectionsDataStore;
import org.softwareFm.card.dataStore.ICardDataStoreCallback;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.future.Futures;
import org.softwareFm.utilities.future.GatedFuture;

/** Note that the callback is called early, but the card may get prodded with valueHasChanged, and the future isn't done until all values have been changed */
public class CardCollectionsDataStore implements ICardAndCollectionsDataStore {

	@Override
	public CardAndCollectionsStatus processDataFor(final ICardHolder cardHolder, final CardConfig cardConfig, final String url, final ICardAndCollectionDataStoreVisitor visitor) {
		final GatedFuture<Void> future = Futures.gatedFuture();
		final AtomicInteger count = new AtomicInteger(1);
		final List<Future<Object>> keyValueFutures = new CopyOnWriteArrayList<Future<Object>>();
		visitor.initialUrl(cardHolder, cardConfig, url);
		Future<ICard> cardFuture = ICardFactory.Utils.makeCard(cardHolder, cardConfig, url, new ICallback<ICard>() {
			@Override
			public void process(final ICard card) throws Exception {
				visitor.initialCard(cardHolder, cardConfig, url, card);
//				cardHolder.setCard(card);
				for (final Entry<String, Object> entry : card.data().entrySet()) {
					final String followOnUrlFragment = cardConfig.followOnFragment.findFollowOnFragment(entry.getKey(), entry.getValue());
					if (followOnUrlFragment != null) {
						count.incrementAndGet();
						visitor.requestingFollowup(cardHolder, url, card, followOnUrlFragment);
						Future<Object> future = cardConfig.cardDataStore.processDataFor(url + "/" + followOnUrlFragment, new ICardDataStoreCallback<Object>() {
							@Override
							public Object process(String followUpUrl, Map<String, Object> result) throws Exception {
								visitor.followedUp(cardHolder, url, card, followUpUrl, result);
								try {
									if (!cardHolder.getControl().isDisposed() && !card.getControl().isDisposed()) {
										Map<String, Object> moreData = cardConfig.modify(url, result);
										card.valueChanged(entry.getKey(), moreData);
										return new LineItem(card.cardType(),entry.getKey(), moreData);
									}
									return null;
								} finally {
									finish(card);
								}
							}

							@Override
							public LineItem noData(String followUpUrl) throws Exception {
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

}
