package org.softwareFm.card.internal.modifiers;

import java.util.List;

import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.IKeyValueListModifier;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.utilities.collections.Lists;

public class KeyValueListSorter implements IKeyValueListModifier {

	@Override
	public List<KeyValue> modify(CardConfig cardConfig, ICard card, List<KeyValue> rawList) {
		return Lists.sort(rawList, cardConfig.comparator);
	}

}
