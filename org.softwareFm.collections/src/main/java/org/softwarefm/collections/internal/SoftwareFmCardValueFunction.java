package org.softwarefm.collections.internal;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.softwareFm.card.card.AbstractLineItemFunction;
import org.softwareFm.card.card.LineItem;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class SoftwareFmCardValueFunction extends AbstractLineItemFunction<String> {
	private final IFunction1<String, IResourceGetter> resourceGetterFn;
	private final String valuePattern;

	public SoftwareFmCardValueFunction(IFunction1<String, IResourceGetter> resourceGetterFn, String valuePattern) {
		this.resourceGetterFn = resourceGetterFn;
		this.valuePattern = valuePattern;
	}

	@Override
	public String apply(LineItem from) throws Exception {
		String key = findKey(from);
		String fullKey = MessageFormat.format(valuePattern, key);
		String pattern = IResourceGetter.Utils.get(resourceGetterFn, from.cardType, fullKey);
		String size = findSize(from);
		if (pattern == null)
			if (from.value instanceof Map<?, ?>)
				return size;
			else
				return Strings.nullSafeToString(from.value);
		else
			return MessageFormat.format(pattern, key, size);
	}

	private String findSize(LineItem from) {
		Object value = from.value;
		if (value instanceof Collection<?>)
			throw new IllegalStateException();
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