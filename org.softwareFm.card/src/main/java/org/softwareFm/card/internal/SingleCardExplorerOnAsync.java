package org.softwareFm.card.internal;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.CardAndCollectionDataStoreVisitorMonitored;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.future.GatedMockFuture;

public class SingleCardExplorerOnAsync {

	public static void main(String[] args) {
		final String rootUrl = CardDataStoreFixture.url;
		Swts.display(SingleCardExplorerOnAsync.class.getSimpleName(), new IFunction1<Composite, Composite>() {

			@Override
			public Composite apply(final Composite from) throws Exception {
				final ICardDataStore cardDataStore = CardDataStoreFixture.rawAsyncCardStore();
				ICardFactory cardFactory = ICardFactory.Utils.cardFactory();
				final CardConfig cardConfig = new BasicCardConfigurator().configure(from.getDisplay(), new CardConfig(cardFactory, cardDataStore));
				final CardHolder cardHolder = new CardHolder(from, "Loading", "title", cardConfig, CardDataStoreFixture.url, ICallback.Utils.<String> noCallback());
				final CardCollectionsDataStore dataStore = new CardCollectionsDataStore() {
					@Override
					protected String findFollowOnUrlFragment(Entry<String, Object> entry) {
						if (entry.getValue() instanceof Map<?, ?>) {
							Map<?, ?> map = (Map<?, ?>) entry.getValue();
							String result = (String) map.get("value");
							return result;
						} else
							return null;
					}
				};

				Thread thread = new Thread() {
					@Override
					public void run() {
						try {
							while (true) {
								final CardAndCollectionsStatus status = dataStore.processDataFor(cardHolder, cardConfig, rootUrl, CardAndCollectionDataStoreVisitorMonitored.Utils.sysout());
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
				Swts.resizeMeToParentsSize(cardHolder.getControl());
				return cardHolder.getComposite();
			}
		});
	}
}
