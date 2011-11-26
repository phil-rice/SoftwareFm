/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card.internal;

import java.util.concurrent.Future;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.configuration.internal.BasicCardConfigurator;
import org.softwareFm.card.dataStore.CardAndCollectionDataStoreVisitorMonitored;
import org.softwareFm.card.dataStore.CardAndCollectionsStatus;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.dataStore.ICardDataStore;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.display.swt.Swts.Size;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.future.GatedMockFuture;

public class SingleCardExplorerOnAsync {

	public static void main(String[] args) {
		final String rootUrl = CardDataStoreFixture.url;
		Show.display(SingleCardExplorerOnAsync.class.getSimpleName(), new IFunction1<Composite, Composite>() {

			@Override
			public Composite apply(final Composite from) throws Exception {
				final ICardDataStore cardDataStore = CardDataStoreFixture.rawAsyncCardStore();
				ICardFactory cardFactory = ICardFactory.Utils.cardFactory();
				final CardConfig cardConfig = new BasicCardConfigurator().configure(from.getDisplay(), new CardConfig(cardFactory, cardDataStore));
				final ICardHolder cardHolder =ICardHolder.Utils.cardHolderWithLayout(from, cardConfig, CardDataStoreFixture.url, ICallback.Utils.<String> noCallback());

				Thread thread = new Thread() {
					@Override
					public void run() {
						try {
							while (true) {
								final CardAndCollectionsStatus status = cardConfig.cardCollectionsDataStore.processDataFor(cardHolder, cardConfig, rootUrl, CardAndCollectionDataStoreVisitorMonitored.Utils.sysout());
								int delay = 1000;
								Thread.sleep(delay);
								((GatedMockFuture<?, ?>) status.initialFuture).kick();
								Thread.sleep(delay);
								for (int i = 0; i < status.keyValueFutures.size(); i++) {
									final Future<Object> f = status.keyValueFutures.get(i);
									Swts.asyncExec(cardHolder, new Runnable() {
										@Override
										public void run() {
											try {
												((GatedMockFuture<?, ?>) f).kick();
												System.out.println(f.get());
											} catch (Exception e) {
												throw WrappedException.wrap(e);
											}
										}
									});
									Thread.sleep(delay);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					};
				};
				thread.setDaemon(true);
				thread.start();
				Size.resizeMeToParentsSize(cardHolder.getControl());
				return cardHolder.getComposite();
			}
		});
	}
}