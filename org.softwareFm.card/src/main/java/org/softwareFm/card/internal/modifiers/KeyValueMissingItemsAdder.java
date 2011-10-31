package org.softwareFm.card.internal.modifiers;

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
		String missing = cardConfig.resourceGetter.getStringOrNull("missing.list");
		if (missing != null) {
			List<String> artifacts = Strings.splitIgnoreBlanks(missing, ",");
			String cardType = card.cardType();
			if (artifacts.contains(cardType)) {
				String missingForSegment = cardConfig.resourceGetter.getStringOrNull("missing." + cardType + ".list");
				if (missingForSegment != null) {
					List<String> missingList = Strings.splitIgnoreBlanks(missingForSegment, ",");
					missingList.removeAll(rawData.keySet());
					if (!missingList.isEmpty()) {
						Map<String, Object> result = Maps.copyMap(rawData);
						for (String missingItem : missingList) {
							String value = IResourceGetter.Utils.getOrException(cardConfig.resourceGetter, "missing." + missingItem + ".default");
							result.put(missingItem, value);
						}
						return result;
					}
				}
			}
		}
		return rawData;
	}
}
