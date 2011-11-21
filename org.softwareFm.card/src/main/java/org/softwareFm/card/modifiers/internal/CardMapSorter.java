package org.softwareFm.card.modifiers.internal;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.softwareFm.card.card.CardConfig;
import org.softwareFm.card.card.ICardDataModifier;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
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
		String cardType = (String) rawData.get(CardConstants.slingResourceType);
		List<String> order = Strings.splitIgnoreBlanks(IResourceGetter.Utils.get(cardConfig.resourceGetterFn, cardType, CardConstants.cardOrderKey), ",");
		Comparator<String> orderedComparator = Lists.orderedComparator(order);
		Map<String, Object> result = Maps.sortByKey(rawData, orderedComparator);
		return result;
	}

}
