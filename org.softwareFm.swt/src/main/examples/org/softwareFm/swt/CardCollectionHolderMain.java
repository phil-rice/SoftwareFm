package org.softwareFm.swt;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.swt.card.dataStore.CardDataStoreFixture;
import org.softwareFm.swt.card.internal.CardCollectionHolder;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.configuration.internal.BasicCardConfigurator;
import org.softwareFm.swt.details.IDetailsFactoryCallback;
import org.softwareFm.swt.swt.Swts;

public class CardCollectionHolderMain {
	public static void main(String[] args) {
		Swts.Show.displayNoLayout(CardCollectionHolder.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(final Composite from) throws Exception {
				Display display = from.getDisplay();
				final CardConfig cardConfig = new BasicCardConfigurator().configure(display, CardDataStoreFixture.syncCardConfig(display));
				IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, null, "navBar.prev.title");
				final CardCollectionHolder cardCollectionHolder = new CardCollectionHolder(from, cardConfig);
				cardCollectionHolder.setKeyValue(CardDataStoreFixture.url, "stuff", Maps.makeMap(CardDataStoreFixture.dataIndexedByUrlFragment), IDetailsFactoryCallback.Utils.resizeAfterGotData());
				return cardCollectionHolder.getComposite();
			}
		});
	}
}
