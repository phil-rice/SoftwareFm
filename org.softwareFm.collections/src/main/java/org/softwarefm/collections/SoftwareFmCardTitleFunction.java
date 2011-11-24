package org.softwarefm.collections;

import org.softwareFm.card.card.ICard;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class SoftwareFmCardTitleFunction implements IFunction1<ICard, String> {

	@Override
	public String apply(ICard from) throws Exception {
		String cardType = from.cardType();
		String nameKey = IResourceGetter.Utils.getOrNull(from.cardConfig().resourceGetterFn, cardType, CardConstants.cardNameFieldKey);
		if (nameKey == null)
			return Strings.lastSegment(from.url(), "/");
		else
			return Strings.nullSafeToString(from.data().get(nameKey));
	}

}
