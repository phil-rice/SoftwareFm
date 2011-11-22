package org.softwareFm.card.card.internal;

import java.text.MessageFormat;

import org.softwareFm.card.card.AbstractLineItemFunction;
import org.softwareFm.card.card.LineItem;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class CardValueFunction extends AbstractLineItemFunction<String> {
	private final IFunction1<String, IResourceGetter> resourceGetterFn;
	private final String valuePattern;

	public CardValueFunction(IFunction1<String, IResourceGetter> resourceGetterFn, String valuePattern) {
		this.resourceGetterFn = resourceGetterFn;
		this.valuePattern = valuePattern;
	}

	@Override
	public String apply(LineItem from) throws Exception {
		String key = findKey(from);
		String fullKey = MessageFormat.format(valuePattern, key);
		String pattern = IResourceGetter.Utils.get(resourceGetterFn, from.cardType, fullKey);
		if (pattern == null)
			return Strings.nullSafeToString(from.value);
		else
			return MessageFormat.format(pattern, key, from.value);
	}
}