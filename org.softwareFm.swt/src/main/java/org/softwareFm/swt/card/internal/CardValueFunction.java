/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.internal;

import java.text.MessageFormat;

import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.swt.card.AbstractLineItemFunction;
import org.softwareFm.swt.card.LineItem;
import org.softwareFm.swt.configuration.CardConfig;

public class CardValueFunction extends AbstractLineItemFunction<String> {
	private final IFunction1<String, IResourceGetter> resourceGetterFn;
	private final String valuePattern;

	public CardValueFunction(IFunction1<String, IResourceGetter> resourceGetterFn, String valuePattern) {
		this.resourceGetterFn = resourceGetterFn;
		this.valuePattern = valuePattern;
	}

	@Override
	public String apply(CardConfig cardConfig, LineItem from) {
		String key = findKey(from);
		String fullKey = MessageFormat.format(valuePattern, key);
		String pattern = IResourceGetter.Utils.get(resourceGetterFn, from.cardType, fullKey);
		if (pattern == null)
			return Strings.nullSafeToString(from.value);
		else
			return MessageFormat.format(pattern, key, from.value);
	}
}