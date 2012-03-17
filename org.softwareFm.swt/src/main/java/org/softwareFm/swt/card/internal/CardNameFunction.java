/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.internal;

import java.text.MessageFormat;

import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.resources.IResourceGetter;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.swt.card.AbstractLineItemFunction;
import org.softwareFm.swt.card.LineItem;
import org.softwareFm.swt.configuration.CardConfig;

public class CardNameFunction extends AbstractLineItemFunction<String> {
	private final IFunction1<String, IResourceGetter> resourceGetterFn;
	private final String namePattern;

	public CardNameFunction(IFunction1<String, IResourceGetter> resourceGetterFn, String namePattern) {
		this.resourceGetterFn = resourceGetterFn;
		this.namePattern = namePattern;
	}

	@Override
	public String apply(CardConfig cardConfig, LineItem from) {
		String key = findKey(from);
		String prettyKey = Strings.camelCaseToPretty(from.key);
		String fullKey = MessageFormat.format(namePattern, key);
		String pattern = IResourceGetter.Utils.get(resourceGetterFn, from.cardType, fullKey);
		if (pattern == null)
			return prettyKey;
		else
			return MessageFormat.format(pattern, key, prettyKey);
	}
}