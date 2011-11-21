package org.softwareFm.card.card.internal;

import java.util.Map;

import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.configuration.CardConfig;

public class CardFactory implements ICardFactory {

	@Override
	public ICard makeCard(ICardHolder cardHolder, final CardConfig cardConfig, String url, Map<String, Object> map) {
		if (cardHolder.getControl().isDisposed())
			return null;
		else {
			final Card card = new Card(cardHolder.getComposite(), cardConfig, url, map);
		
			cardHolder.setCard(card);
			return card;
		}
	}

}
