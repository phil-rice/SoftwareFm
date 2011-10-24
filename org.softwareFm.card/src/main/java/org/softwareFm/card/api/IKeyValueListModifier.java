package org.softwareFm.card.api;

import java.util.List;

public interface IKeyValueListModifier {

	public List<KeyValue> modify(CardConfig cardConfig, ICard card, List<KeyValue> rawList);

}
