package org.softwareFm.card.internal;

import java.util.Map;

import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataModifier;

public class MockKeyValueModifier implements ICardDataModifier {
	public Map<String, Object> result;
	public ICard card;
	public Map<String, Object> rawData;

	public MockKeyValueModifier(Map<String,Object> result) {
		this.result = result;
	}

	@Override
	public Map<String, Object> modify(CardConfig cardConfig, String url, Map<String, Object> rawData) {
		this.card = url;
		this.rawData = rawData;
		return result;
	}

}
