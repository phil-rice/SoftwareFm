package org.softwareFm.card.internal.modifiers;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICardDataModifier;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class KeyValueMissingItemsAdder implements ICardDataModifier {

	@Override
	public Map<String, Object> modify(CardConfig cardConfig, String url, Map<String, Object> rawData) {
		String cardType = (String) rawData.get(CardConstants.slingResourceType);
		List<String> missingStrings = Strings.splitIgnoreBlanks(IResourceGetter.Utils.get(cardConfig.resourceGetterFn, cardType, "missing." + cardType + ".string"), ",");
		List<String> missingLists = Strings.splitIgnoreBlanks(IResourceGetter.Utils.get(cardConfig.resourceGetterFn, cardType, "missing." + cardType + ".list"), ",");
		missingLists.removeAll(rawData.keySet());
		missingStrings.removeAll(rawData.keySet());
		missingStrings.removeAll(missingLists); // just in case
		if (!missingLists.isEmpty() || !missingStrings.isEmpty()) {
			Map<String, Object> result = Maps.copyMap(rawData);
			for (String item : missingStrings) {
				String value = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn,cardType, "missing." + item + ".default");
				result.put(item, value);
			}
			for (String item : missingLists)
				result.put(item, Collections.emptyMap());
			return result;
		}
		return rawData;
	}
}
