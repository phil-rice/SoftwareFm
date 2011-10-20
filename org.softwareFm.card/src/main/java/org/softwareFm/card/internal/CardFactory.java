package org.softwareFm.card.internal;

import java.util.Map;

import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardFactory;

public class CardFactory implements ICardFactory {

	@Override
	public ICard makeCard(CardHolder cardHolder, CardConfig cardConfig, String url, Map<String, Object> map) {
		if (cardHolder.getControl().isDisposed())
			return null;
		else {
			final Card card = new Card(cardHolder.getComposite(), cardConfig, url, map);
			return card;
		}
	}

}
