package org.softwareFm.card.card.internal;

import java.util.Map;

import org.softwareFm.card.card.CardConfig;
import org.softwareFm.card.card.ICardDataModifier;

public class MockKeyValueModifier implements ICardDataModifier {
	public Map<String, Object> result;
	public Map<String, Object> rawData;
	public String url;

	public MockKeyValueModifier(Map<String,Object> result) {
		this.result = result;
	}

	@Override
	public Map<String, Object> modify(CardConfig cardConfig, String url, Map<String, Object> rawData) {
		this.url = url;
		this.rawData = rawData;
		return result;
	}

}
