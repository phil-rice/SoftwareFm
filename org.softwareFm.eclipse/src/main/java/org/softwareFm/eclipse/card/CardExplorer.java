package org.softwareFm.eclipse.card;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardDataStoreCallback;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.ICardFactoryWithAggregateAndSort;
import org.softwareFm.card.api.ILine;
import org.softwareFm.card.api.ILineSelectedListener;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.card.api.MultipleCardsWithScroll;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.repositoryFacard.IRepositoryFacardCallback;
import org.softwareFm.repositoryFacardConstants.RepositoryFacardConstants;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;

public class CardExplorer {
	public static void main(String[] args) throws IOException {
		final ICardFactoryWithAggregateAndSort cardFactory = ICardFactory.Utils.cardFactoryWithAggregateAndSort();
		final IRepositoryFacard facard = IRepositoryFacard.Utils.defaultFacard();
		try {
			final ICardDataStore cardDataStore =  ICardDataStore.Utils.cache(new ICardDataStore() {
				@Override
				public Future<?> processDataFor(final String url, final ICardDataStoreCallback callback) {
					return facard.get(url, new IRepositoryFacardCallback() {
						@Override
						public void process(IResponse response, Map<String, Object> data) {
							try {
								if (RepositoryFacardConstants.okStatusCodes.contains(response.statusCode()))
									callback.process(url, data);
								else {
									System.out.println(response.asString());
									callback.noData(url);
								}
							} catch (Exception e) {
								throw WrappedException.wrap(e);
							}
						}
					});
				}
			});
			final String url = "/softwareFm/repository/org";
			Swts.displayNoLayout(CardExplorer.class.getSimpleName(), new IFunction1<Composite, Composite>() {
				@Override
				public Composite apply(final Composite from) throws Exception {
					final MultipleCardsWithScroll content = new MultipleCardsWithScroll(from, cardDataStore, cardFactory);
					final ICallback<ICard> callback = new ICallback<ICard>() {
						@Override
						public void process(ICard t) throws Exception {
							layout(content.getComposite(), content);
						}
					};
					openChild(null, url, content, callback);
					final Control control = content.getControl();
					from.addListener(SWT.Resize, new Listener() {
						@Override
						public void handleEvent(Event event) {
							Rectangle clientArea = from.getClientArea();
							control.setSize(clientArea.width, clientArea.height);
						}
					});
					from.addListener(SWT.Resize, new Listener() {
						@Override
						public void handleEvent(Event event) {
							layout(content.getComposite(), content);
						}
					});
					layout(content.getComposite(), content);
					return (Composite) control;
				}

				private void openChild(ICard parent, final String url, final MultipleCardsWithScroll content, final ICallback<ICard> callback) {
					final ICard card = content.openCardAsChildOf(parent, url, callback);
					card.addLineSelectedListener(new ILineSelectedListener() {
						@Override
						public void selected(KeyValue keyValue, ILine line) {
							String newUrl = url + "/" + keyValue.key;
							openChild(card, newUrl, content, callback);
						}
					});
				}

				private void layout(final Composite from, final MultipleCardsWithScroll multipleCards) {
					Rectangle clientArea = from.getClientArea();
					Swts.setSizeToComputedAndLayout(multipleCards, clientArea);
					Swts.setSizeToComputedAndLayout(multipleCards.content, SWT.DEFAULT, clientArea.height);
					System.out.println("Scroll: " + multipleCards.getComposite().getClientArea() + ", cards: " + multipleCards.content.getControl().getSize());
				}
			});
		} finally {
			facard.shutdown();
		}
	}
}
