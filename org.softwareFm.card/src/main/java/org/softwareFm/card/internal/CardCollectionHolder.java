package org.softwareFm.card.internal;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.CardAndCollectionDataStoreVisitorMock;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.ICardHolder;
import org.softwareFm.card.api.ICardSelectedListener;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

public class CardCollectionHolder implements IHasComposite {

	private final CardCollectionHolderComposite content;
	String rootUrl;

	static class CardCollectionHolderComposite extends Composite {

		private final CardConfig cardConfig;
		private final List<ICardSelectedListener> listeners = new CopyOnWriteArrayList<ICardSelectedListener>();
		private final CardCollectionsDataStore cardCollectionsDataStore = new CardCollectionsDataStore();
		private String key;
		private Object value;

		public CardCollectionHolderComposite(Composite parent, CardConfig cardConfig) {
			super(parent, SWT.NULL);
			this.cardConfig = cardConfig;
			setLayout(new FillWithAspectRatioLayoutManager(3, 2));
		}

		@SuppressWarnings("unchecked")
		protected void setKeyValue(final String rootUrl, String key, Object value) {
			this.key = key;
			this.value = value;
			Swts.removeAllChildren(this);
			if (value instanceof Map<?,?>) {
				Map<String, ?> map = Maps.sortByKey((Map<String,?>) value, cardConfig.comparator);
				for (final Map.Entry<String, ?> childEntry: map.entrySet()){
					final CardHolder cardHolder = new CardHolder(this, "loading", childEntry.getKey(), cardConfig, rootUrl, null);
					cardHolder.getControl().addPaintListener(new PaintListener() {
						@Override
						public void paintControl(PaintEvent e) {
							cardHolder.getControl().removePaintListener(this);
							if (!cardHolder.getControl().isDisposed()) {
								CardAndCollectionDataStoreVisitorMock visitor = new CardAndCollectionDataStoreVisitorMock() {
									@Override
									public void initialUrl(ICardHolder cardHolder, CardConfig cardConfig, String url) {
									}

									@Override
									public void initialCard(ICardHolder cardHolder, CardConfig cardConfig, String url, final ICard card) {
										card.getControl().addMouseListener(new MouseAdapter() {
											@Override
											public void mouseUp(org.eclipse.swt.events.MouseEvent e) {
												for (ICardSelectedListener listener : listeners) {
													listener.cardSelected(card);
												}
											}
										});
									}

									@Override
									public void requestingFollowup(ICardHolder cardHolder, String url, ICard card, String followOnUrlFragment) {
									}

									@Override
									public void followedUp(ICardHolder cardHolder, String url, ICard card, String followUpUrl, Map<String, Object> result) {
									}

									@Override
									public void noData(ICardHolder cardHolder, String url, ICard card, String followUpUrl) {
									}

									@Override
									public void finished(ICardHolder cardHolder, String url, ICard card) {
									}
								};
								cardCollectionsDataStore.processDataFor(cardHolder, cardConfig, rootUrl + "/" + childEntry.getKey(), visitor);
							}
						};

					});
				}
			}
			layout();
		}
	}

	public void addCardSelectedListener(ICardSelectedListener listener) {
		content.listeners.add(listener);
	}

	public CardCollectionHolder(Composite parent, CardConfig cardConfig) {
		content = new CardCollectionHolderComposite(parent, cardConfig);
	}

	public void setKeyValue(final String rootUrl, String key, Object value) {
		this.rootUrl = rootUrl;
		content.setKeyValue(rootUrl, key, value);
	}

	public String getRootUrl() {
		return rootUrl;
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	public CardConfig getCardConfig() {
		return content.cardConfig;

	}

	public String getKey() {
		return content.key;
	}

	public Object getValue() {
		return content.value;
	}

	public static void main(String[] args) {
		Swts.displayNoLayout(CardCollectionHolder.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(final Composite from) throws Exception {
				final ICardDataStore cardDataStore = CardDataStoreFixture.rawCardStore();
				ICardFactory cardFactory = ICardFactory.Utils.cardFactory();
				final CardConfig cardConfig = new BasicCardConfigurator().configure(from.getDisplay(), new CardConfig(cardFactory, cardDataStore));
				IResourceGetter.Utils.getOrException(cardConfig.resourceGetter, "navBar.prev.title");
				final CardCollectionHolder cardCollectionHolder = new CardCollectionHolder(from, cardConfig);
				cardCollectionHolder.setKeyValue(CardDataStoreFixture.url, "stuff", Maps.makeMap(CardDataStoreFixture.dataIndexedByUrlFragment));
				Swts.resizeMeToParentsSize(cardCollectionHolder.getControl());
				return cardCollectionHolder.getComposite();
			}
		});
	}
}
