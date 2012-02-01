/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.collections.internal;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.softwareFm.card.card.AbstractLineItemFunction;
import org.softwareFm.card.card.LineItem;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.strings.Strings;

public class SoftwareFmCardValueFunction extends AbstractLineItemFunction<String> {
	private final IFunction1<String, IResourceGetter> resourceGetterFn;
	private final String valuePattern;

	public SoftwareFmCardValueFunction(IFunction1<String, IResourceGetter> resourceGetterFn, String valuePattern) {
		this.resourceGetterFn = resourceGetterFn;
		this.valuePattern = valuePattern;
	}

	@Override
	public String apply(CardConfig cardConfig, LineItem from) {
		String key = findKey(from);
		String fullKey = MessageFormat.format(valuePattern, key);
		String pattern = IResourceGetter.Utils.get(resourceGetterFn, from.cardType, fullKey);
		String size = findSize(from);
		if (pattern == null)
			if (from.value instanceof Map<?, ?>)
				if (CardConstants.collection.equals(from.cardType))
					return "";
				else
					return size;
			else
				return Strings.nullSafeToString(from.value);
		else
			return MessageFormat.format(pattern, key, size);
	}

	private String findSize(LineItem from) {
		Object value = from.value;
		if (value instanceof Collection<?>)
			throw new IllegalStateException(from.toString());
		else if (value instanceof Map<?, ?>) {
			int i = 0;
			for (Entry<?, ?> entry : ((Map<?, ?>) value).entrySet())
				if (entry.getValue() instanceof Map<?, ?>)
					i++;
			return Integer.toString(i);
		} else
			return Strings.nullSafeToString(from.value);
	}
}