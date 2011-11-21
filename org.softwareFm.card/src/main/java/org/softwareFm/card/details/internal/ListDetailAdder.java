package org.softwareFm.card.details.internal;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.internal.ListOfCards;
import org.softwareFm.card.card.internal.ScrollingCardCollectionHolder;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.details.IDetailAdder;
import org.softwareFm.card.details.IDetailsFactoryCallback;
import org.softwareFm.display.composites.IHasControl;

public class ListDetailAdder implements IDetailAdder {
	@Override
	public IHasControl add(Composite parentComposite, ICard parentCard, CardConfig cardConfig, String key, Object value, IDetailsFactoryCallback callback) {
		if (value instanceof Map<?, ?>) {
			if (useListOfCards) {
				ListOfCards result = new ListOfCards(parentComposite, cardConfig);
				result.setKeyValue(parentCard.url(), "", key, value, callback);
				return result;
			} else {
				ScrollingCardCollectionHolder result = new ScrollingCardCollectionHolder(parentComposite, cardConfig);
				result.setKeyValue(parentCard.url(), key, value, callback);
				return result;
			}
		} else
			return null;
	}

}
