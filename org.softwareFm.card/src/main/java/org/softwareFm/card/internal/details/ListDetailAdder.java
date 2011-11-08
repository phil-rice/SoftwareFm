package org.softwareFm.card.internal.details;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.IDetailsFactoryCallback;
import org.softwareFm.card.internal.CardCollectionHolder;
import org.softwareFm.display.composites.IHasControl;

public class ListDetailAdder implements IDetailAdder {
	@Override
	public IHasControl add(Composite parentComposite, ICard parentCard, CardConfig cardConfig, String key, Object value, IDetailsFactoryCallback callback) {
		if (value instanceof Map<?,?>) {
			CardCollectionHolder result = new CardCollectionHolder(parentComposite, cardConfig);
			result.setKeyValue(parentCard.url(), key, value,callback);
			return result;
		} else
			return null;
	}

}
