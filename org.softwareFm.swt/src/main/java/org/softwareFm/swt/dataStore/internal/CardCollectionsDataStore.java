/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.dataStore.internal;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.transaction.GatedTransaction;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.card.ICardHolder;
import org.softwareFm.swt.card.LineItem;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.dataStore.CardAndCollectionsStatus;
import org.softwareFm.swt.dataStore.ICardAndCollectionDataStoreVisitor;
import org.softwareFm.swt.dataStore.ICardAndCollectionsDataStore;
import org.softwareFm.swt.dataStore.ICardDataStoreCallback;

/** Note that the callback is called early, but the card may get prodded with valueHasChanged, and the future isn't done until all values have been changed */
public class CardCollectionsDataStore implements ICardAndCollectionsDataStore {

	@Override
	public CardAndCollectionsStatus processDataFor(final ICardHolder cardHolder, final CardConfig cardConfig, final String url, final ICardAndCollectionDataStoreVisitor visitor) {
		final GatedTransaction<Void> mainTransaction = ITransaction.Utils.gatedTransaction();
		final AtomicInteger count = new AtomicInteger(1);
		final List<ITransaction<Object>> keyValueFutures = new CopyOnWriteArrayList<ITransaction<Object>>();
		visitor.initialUrl(cardHolder, cardConfig, url);
		ITransaction<ICard> cardFuture = ICardFactory.Utils.makeCard(cardHolder, cardConfig, url, new ICallback<ICard>() {
			@Override
			public void process(final ICard card) throws Exception {
				visitor.initialCard(cardHolder, cardConfig, url, card);
				// cardHolder.setCard(card);
				for (final Entry<String, Object> entry : card.data().entrySet()) {
					final String followOnUrlFragment = cardConfig.followOnFragment.findFollowOnFragment(entry.getKey(), entry.getValue());
					if (followOnUrlFragment != null) {
						count.incrementAndGet();
						visitor.requestingFollowup(cardHolder, url, card, followOnUrlFragment);
						ITransaction<Object> future = cardConfig.cardDataStore.processDataFor(url + "/" + followOnUrlFragment, new ICardDataStoreCallback<Object>() {
							@Override
							public Object process(String followUpUrl, Map<String, Object> result) throws Exception {
								visitor.followedUp(cardHolder, url, card, followUpUrl, result);
								try {
									if (!cardHolder.getControl().isDisposed() && !card.getControl().isDisposed()) {
										Map<String, Object> moreData = cardConfig.modify(url, result);
										card.valueChanged(entry.getKey(), moreData);
										return new LineItem(card.cardType(), entry.getKey(), moreData);
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
					try {
						visitor.finished(cardHolder, url, card);
					} catch (Exception e) {
						throw WrappedException.wrap(e);
					}
					mainTransaction.kick();
				}
			}
		});
		final CardAndCollectionsStatus status = new CardAndCollectionsStatus(mainTransaction, cardFuture, keyValueFutures, count);
		return status;
	}

}