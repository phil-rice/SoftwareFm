package org.softwareFm.card.internal;

import java.util.List;

import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataModifier;
import org.softwareFm.card.api.KeyValue;

public class MockKeyValueModifier implements ICardDataModifier {
	public List<KeyValue> result;
	public List<KeyValue> rawList;
	public ICard card;

	public MockKeyValueModifier(List<KeyValue> result) {
		this.result = result;
	}

	@Override
	public List<KeyValue> modify(CardConfig cardConfig, ICard card, List<KeyValue> rawList) {
		this.card = card;
		this.rawList = rawList;
		return result;
	}

}
