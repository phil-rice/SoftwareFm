package org.softwareFm.card.api;

import java.util.Map;

public class CardFactoryMock implements ICardFactory{

	public int count;

	@Override
	public ICard makeCard(ICardHolder cardHolder, CardConfig cardConfig, String url, Map<String, Object> map) {
		count++;
		return new CardMock(cardHolder, cardConfig, url, map);
	}

}
