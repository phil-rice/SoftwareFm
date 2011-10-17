package org.softwareFm.eclipse.card;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardDataStoreCallback;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.ILine;
import org.softwareFm.card.api.ILineSelectedListener;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.card.api.MultipleCardsWithScroll;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.repositoryFacard.IRepositoryFacardCallback;
import org.softwareFm.repositoryFacardConstants.RepositoryFacardConstants;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;

public class PathAndDetails implements IHasComposite {

	private final Composite content;

	static class PathAndDetailsComposite extends SashForm {
		private final List<String> urls = Lists.newList();
		private final MultipleCardsWithScroll multipleCards;

		public PathAndDetailsComposite(Composite parent, ICardDataStore cardDataStore, ICardFactory cardFactory, final String initialUrl) {
			super(parent, SWT.NULL);
			final ICard card = cardFactory.makeCard(this, cardDataStore, initialUrl);
			multipleCards = new MultipleCardsWithScroll(this, cardDataStore, cardFactory);
			setWeights(new int[] { 1, 3 });
			card.addLineSelectedListener(new ILineSelectedListener() {
				@Override
				public void selected(KeyValue keyValue, ILine line) {
					try {
						String newUrl = initialUrl + "/" + keyValue.key;
						System.out.println("opening cards. Content: " + multipleCards.getComposite().getClientArea());
						Object value = keyValue.value;
						if (value instanceof Map) {
							Map<String, Object> map = (Map<String, Object>) value;
							for (Entry<String, Object> entry : map.entrySet())
								if (entry.getValue() instanceof Map)
									multipleCards.openCardAsChildOf(null, newUrl + "/" + entry.getKey()).getPopulateFuture().get();
						}
					} catch (Exception e) {
						throw WrappedException.wrap(e);
					}
				}
			});
		}

		private void layoutScrolledArea() {
			Rectangle clientArea = getClientArea();
			Swts.setSizeToComputedAndLayout(multipleCards, clientArea);
			Swts.setSizeToComputedAndLayout(multipleCards.content, SWT.DEFAULT, SWT.DEFAULT);
			System.out.println("Scroll: " + multipleCards.scroll.getClientArea() + ", cards: " + multipleCards.content.getControl().getSize());
		}

	}

	public PathAndDetails(Composite parent, ICardDataStore cardDataStore, ICardFactory cardFactory, String initialUrl) {
		content = new PathAndDetailsComposite(parent, cardDataStore, cardFactory, initialUrl);

	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	public static void main(String[] args) {
		final ICardFactory cardFactory = ICardFactory.Utils.cardFactoryWithAggregateAndSort();
		final IRepositoryFacard facard = IRepositoryFacard.Utils.defaultFacard();
		try {
			final ICardDataStore cardDataStore = ICardDataStore.Utils.cache(new ICardDataStore() {
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

			Swts.display(PathAndDetails.class.getSimpleName(), new IFunction1<Composite, Composite>() {
				@Override
				public Composite apply(Composite from) throws Exception {
					PathAndDetails pathAndDetails = new PathAndDetails(from, cardDataStore, cardFactory, url);
					return pathAndDetails.getComposite();
				}
			});

		} finally {
			facard.shutdown();
		}
	}
}
