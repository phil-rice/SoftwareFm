package org.softwareFm.card.card;

import java.util.Map;

import org.softwareFm.card.card.CardConfig;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.card.ICardHolder;

public class CardFactoryMock implements ICardFactory {

	public int count;

	@Override
	public ICard makeCard(ICardHolder cardHolder, CardConfig cardConfig, String url, Map<String, Object> map) {
		count++;
		CardMock card = new CardMock(cardHolder, cardConfig, url, map);
		cardHolder.setCard(card);
		return card;
	}

}
