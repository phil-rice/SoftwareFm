package org.softwareFm.card.internal.modifiers;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataModifier;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class KeyValueMissingItemsAdder implements ICardDataModifier {

	@Override
	public Map<String, Object> modify(CardConfig cardConfig, ICard card, Map<String, Object> rawData) {
		String missingEntityList = cardConfig.resourceGetter.getStringOrNull("missing.list");
		if (missingEntityList != null) {
			List<String> entities = Strings.splitIgnoreBlanks(missingEntityList, ",");
			String cardType = card.cardType();
			if (entities.contains(cardType)) {
				List<String> missingStrings = Strings.splitIgnoreBlanks(cardConfig.resourceGetter.getStringOrNull("missing." + cardType + ".string"), ",");
				List<String> missingLists = Strings.splitIgnoreBlanks(cardConfig.resourceGetter.getStringOrNull("missing." + cardType + ".list"), ",");
				missingLists.removeAll(rawData.keySet());
				missingStrings.removeAll(rawData.keySet());
				missingStrings.removeAll(missingLists); //just in case
				if (!missingLists.isEmpty()||!missingStrings.isEmpty()) {
					Map<String, Object> result = Maps.copyMap(rawData);
					for (String item : missingStrings) {
						String value = IResourceGetter.Utils.getOrException(cardConfig.resourceGetter, "missing." + item + ".default");
						result.put(item, value);
					}
					for (String item: missingLists)
						result.put(item, Collections.emptyMap());
					return result;
				}
			}
		}
		return rawData;
	}
}
