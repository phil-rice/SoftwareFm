package org.softwareFm.card.api;

import java.util.Map;

public interface ICardDataModifier {

	public Map<String,Object> modify(CardConfig cardConfig, ICard card, Map<String,Object> rawData);

}
