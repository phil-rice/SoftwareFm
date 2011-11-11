package org.softwareFm.card.internal.modifiers;

import java.util.Comparator;
import java.util.Map;

import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICardDataModifier;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Strings;

public class CardMapSorter implements ICardDataModifier {

	
	private final String version;
	public CardMapSorter(String version) {
		this.version = version;
	}
	@Override
	public Map<String, Object> modify(CardConfig cardConfig, String url, Map<String, Object> rawData) {
		String lastSegment = Strings.lastSegment(url, "/");
		if (lastSegment.equals(version))
			return Maps.sortByKey(rawData, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return Strings.compareVersionNumbers(o1,o2);
				}
			});
		return Maps.sortByKey(rawData, cardConfig.comparator);
	}

}
