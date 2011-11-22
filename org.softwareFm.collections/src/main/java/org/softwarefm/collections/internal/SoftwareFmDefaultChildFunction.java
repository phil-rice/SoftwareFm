package org.softwarefm.collections.internal;

import java.util.Map;
import java.util.Map.Entry;

import org.softwareFm.card.card.ICard;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;

public class SoftwareFmDefaultChildFunction implements IFunction1<ICard, String> {
	final Map<String, String> typeToDefaultChildMap = Maps.makeMap(//
			CardConstants.group, CardConstants.artifact,//
			CardConstants.artifact, CardConstants.version,//
			CardConstants.version, CardConstants.digest);

	@Override
	public String apply(ICard from) throws Exception {
		String cardType = typeToDefaultChildMap.get(from.cardType());
		Map<String, Object> data = from.data();
		if (data.containsKey(cardType))
			return cardType;
		if (data.containsKey("nt:unstructured"))
			return "nt:unstructured";
		for (Entry<String, Object> entry : data.entrySet())
			if (entry.getValue() instanceof Map<?, ?>) {
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) entry.getValue();
				if (CardConstants.group.equals(map.get(CardConstants.slingResourceType)))
					return entry.getKey();
			}
		return null;
	}
}