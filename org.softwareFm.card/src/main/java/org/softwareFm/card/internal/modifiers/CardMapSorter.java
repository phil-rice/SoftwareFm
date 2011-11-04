package org.softwareFm.card.internal.modifiers;

import java.util.Map;

import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICardDataModifier;
import org.softwareFm.utilities.maps.Maps;

public class CardMapSorter implements ICardDataModifier {

	@Override
	public Map<String, Object> modify(CardConfig cardConfig, String url, Map<String, Object> rawData) {
		return Maps.sortByKey(rawData, cardConfig.comparator);
	}

}
