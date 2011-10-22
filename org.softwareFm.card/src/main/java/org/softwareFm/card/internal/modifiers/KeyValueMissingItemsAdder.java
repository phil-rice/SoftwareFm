package org.softwareFm.card.internal.modifiers;

import java.util.ArrayList;
import java.util.List;

import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.IKeyValueListModifier;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.strings.Strings;

public class KeyValueMissingItemsAdder implements IKeyValueListModifier {

	@Override
	public List<KeyValue> modify(CardConfig cardConfig, ICard card, List<KeyValue> rawList) {
		String missing = cardConfig.resourceGetter.getStringOrNull("missing.list");
		if (missing != null) {
			List<String> artifacts = Strings.splitIgnoreBlanks(missing, ",");
			String lastSegment = Functions.call(Strings.lastSegmentFn("/"), card.url());
			if (artifacts.contains(lastSegment)) {
				String missingForSegment = cardConfig.resourceGetter.getStringOrNull("missing." + lastSegment + ".list");
				if (missingForSegment != null) {
					List<String> missingList = Strings.splitIgnoreBlanks(missingForSegment, ",");
					for (KeyValue kv : rawList) {
						if (missingList.contains(kv.key))
							missingList.remove(kv.key);
					}
					if (!missingList.isEmpty()) {
						List<KeyValue> results = new ArrayList<KeyValue>(rawList);
						for (String missingItem : missingList) {
							String value = cardConfig.resourceGetter.getStringOrNull("missing." + missingItem + ".default");
							results.add(new KeyValue(missingItem, Strings.nullSafeToString(value)));
						}
						return results;
					}
				}

			}
		}
		return rawList;
	}
}
