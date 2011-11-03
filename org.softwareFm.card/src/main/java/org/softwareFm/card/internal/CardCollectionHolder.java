package org.softwareFm.card.internal;

import java.util.Map;
import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.internal.details.IDetailsFactoryCallback;
import org.softwareFm.card.internal.details.IGotDataCallback;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

public class CardCollectionHolder implements IHasComposite {

	String rootUrl;
	private final CardCollectionHolderComposite content;

	public static class CardCollectionHolderComposite extends HoldsCardHolder {

		private String key;
		private Object value;

		public CardCollectionHolderComposite(Composite parent, CardConfig cardConfig) {
			super(parent, SWT.NULL,cardConfig);
			setLayout(new FillWithAspectRatioLayoutManager(3, 2));
		}

		@SuppressWarnings("unchecked")
		protected Future<?> setKeyValue(final String rootUrl, String key, Object value, final IGotDataCallback callback) {
			if (isDisposed())
				return null;
			this.key = key;
			this.value = value;
			Swts.removeAllChildren(this);
			if (value instanceof Map<?, ?>) {
				Map<String, ?> map = Maps.sortByKey((Map<String, ?>) value, cardConfig.comparator);
				for (final Map.Entry<String, ?> childEntry : map.entrySet()) {
					if (childEntry.getValue() instanceof Map<?, ?>) {
						String detailUrl = rootUrl + "/" + childEntry.getKey();
						String title = childEntry.getKey();
						makeCardHolder(detailUrl, title);
					}
				}
				callback.gotData(CardCollectionHolderComposite.this);
			}
			return null;
		}
	}

	

	public CardCollectionHolder(Composite parent, CardConfig cardConfig) {
		content = new CardCollectionHolderComposite(parent, cardConfig);
	}

	public void setKeyValue(final String rootUrl, String key, Object value, IDetailsFactoryCallback callback) {
		this.rootUrl = rootUrl;
		content.setKeyValue(rootUrl, key, value, callback);
		content.addCardSelectedListener(callback);
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
				cardCollectionHolder.setKeyValue(CardDataStoreFixture.url, "stuff", Maps.makeMap(CardDataStoreFixture.dataIndexedByUrlFragment), IDetailsFactoryCallback.Utils.resizeAfterGotData(cardCollectionHolder));
				return cardCollectionHolder.getComposite();
			}
		});
	}
}
