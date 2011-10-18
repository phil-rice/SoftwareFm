package org.softwareFm.card.internal;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardFactory;

public class CardFactory implements ICardFactory {

	@Override
	public ICard makeCard(Composite parent, CardConfig cardConfig, String url, Map<String, Object> map) {
		if (parent.isDisposed())
			return null;
		else {
			final Card card = new Card(parent, cardConfig, url, map);
			return card;
		}
	}

}
