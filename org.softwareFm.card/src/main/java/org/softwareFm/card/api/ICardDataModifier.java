package org.softwareFm.card.api;

import java.util.Map;

public interface ICardDataModifier {

	public Map<String,Object> modify(CardConfig cardConfig, String url, Map<String,Object> rawData);

}
