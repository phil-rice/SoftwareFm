package org.softwareFm.card.internal;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardDataStoreCallback;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

public class CardCollectionHolder implements IHasComposite {

	private final CardCollectionHolderComposite content;

	static class CardCollectionHolderComposite extends Composite {

		private final CardConfig cardConfig;

		public CardCollectionHolderComposite(Composite parent, CardConfig cardConfig) {
			super(parent, SWT.NULL);
			this.cardConfig = cardConfig;
			setLayout(new FillWithAspectRatioLayoutManager(3, 2));
		}

		protected void setKeyValue(final String rootUrl, KeyValue keyValue) {
			Object value = keyValue.value;
			Swts.removeAllChildren(this);
			if (value instanceof Map<?, ?>) {
				for (final Entry<String, Object> entry : ((Map<String, Object>) value).entrySet()) {
					final CardHolder cardHolder = new CardHolder(this, "loading", entry.getKey(), cardConfig, rootUrl, null);
					cardHolder.getControl().addPaintListener(new PaintListener() {
						@Override
						public void paintControl(PaintEvent e) {
							cardHolder.getControl().removePaintListener(this);
							cardConfig.cardDataStore.processDataFor(rootUrl + "/" + entry.getKey(), new ICardDataStoreCallback<Void>() {
								@Override
								public Void process(String url, Map<String, Object> result) throws Exception {
									if (!cardHolder.getControl().isDisposed())
										cardConfig.cardFactory.makeCard(cardHolder, cardConfig, url, result);

									System.out.println("seting card in card holder in collection holder: " + url + "   " + result);
									return null;
								}

								@Override
								public Void noData(String url) throws Exception {
									return process(url, Collections.<String, Object> emptyMap());
								}
							});
						}
					});
				}
			}
			layout();
			System.out.println("set data: ");
			Swts.layoutDump(this);
		}
	}

	public CardCollectionHolder(Composite parent, CardConfig cardConfig) {
		content = new CardCollectionHolderComposite(parent, cardConfig);
	}

	protected void setKeyValue(final String rootUrl, KeyValue keyValue) {
		content.setKeyValue(rootUrl, keyValue);
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
		Swts.displayNoLayout(CardCollectionHolder.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(final Composite from) throws Exception {
				final ICardDataStore cardDataStore = CardDataStoreFixture.rawCardStore();
				ICardFactory cardFactory = ICardFactory.Utils.cardFactory();
				final CardConfig cardConfig = new BasicCardConfigurator().configure(from.getDisplay(), new CardConfig(cardFactory, cardDataStore));
				IResourceGetter.Utils.getOrException(cardConfig.resourceGetter, "navBar.prev.title");
				final CardCollectionHolder cardCollectionHolder = new CardCollectionHolder(from, cardConfig);
				cardCollectionHolder.setKeyValue(CardDataStoreFixture.url, new KeyValue("stugg", Maps.makeMap(CardDataStoreFixture.dataIndexedByUrlFragment)));
				Swts.resizeMeToParentsSize(cardCollectionHolder.getControl());
				return cardCollectionHolder.getComposite();
			}
		});
	}
}
