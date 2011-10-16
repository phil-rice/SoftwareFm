package org.softwareFm.eclipse.card;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardDataStoreCallback;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.ICardFactoryWithAggregateAndSort;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.repositoryFacard.IRepositoryFacardCallback;
import org.softwareFm.repositoryFacardConstants.RepositoryFacardConstants;
import org.softwareFm.utilities.functions.IFunction1;

public class CardExplorer {
	public static void main(String[] args) throws IOException {
		final ICardFactoryWithAggregateAndSort cardFactory = ICardFactory.Utils.cardFactoryWithAggregateAndSort();
		final IRepositoryFacard facard = IRepositoryFacard.Utils.defaultFacard();
		try {
			final ICardDataStore cardDataStore = new ICardDataStore() {
				@Override
				public Future<?> processDataFor(final String url, final ICardDataStoreCallback callback) {
					return facard.get(url, new IRepositoryFacardCallback() {
						@Override
						public void process(IResponse response, Map<String, Object> data) {
							if (RepositoryFacardConstants.okStatusCodes.contains(response.statusCode()))
								callback.process(url, data);
							else {
								System.out.println(response.asString());
								callback.noData(url);
							}
						}
					});
				}
			};
			final String url = "/";
			Swts.display(CardExplorer.class.getSimpleName(), new IFunction1<Composite, Composite>() {

				@Override
				public Composite apply(Composite from) throws Exception {
					SashForm holder = new SashForm(from, SWT.VERTICAL);
					cardFactory.makeCard(holder, cardDataStore, url);
					return holder;
				}
			});
		} finally {
			facard.shutdown();
		}
	}
}
