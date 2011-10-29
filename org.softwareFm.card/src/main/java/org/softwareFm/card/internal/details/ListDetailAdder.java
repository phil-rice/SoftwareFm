package org.softwareFm.card.internal.details;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardSelectedListener;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.card.internal.CardCollectionHolder;
import org.softwareFm.display.composites.IHasControl;

public class ListDetailAdder implements IDetailAdder {
	@Override
	public IHasControl add(Composite parentComposite, ICard parentCard, CardConfig cardConfig, KeyValue keyValue, ICardSelectedListener listener, Runnable afterEdit) {
		if (keyValue.value instanceof List<?>) {
			CardCollectionHolder result = new CardCollectionHolder(parentComposite, cardConfig);
			result.setKeyValue(parentCard.url(), keyValue);
			result.addCardSelectedListener(listener);
			return result;
		} else
			return null;
	}

}
