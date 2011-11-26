/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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