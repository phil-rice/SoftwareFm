package org.softwareFm.card.card.internal;

import java.text.MessageFormat;
import java.util.Map;

import org.softwareFm.card.card.LineItem;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class CardNameFunction extends AbstractLineItemToStringFunction<String> {
	private final IFunction1<String, IResourceGetter> resourceGetterFn;
	private final String namePattern;

	public CardNameFunction(IFunction1<String, IResourceGetter> resourceGetterFn, String namePattern) {
		this.resourceGetterFn = resourceGetterFn;
		this.namePattern = namePattern;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String apply(LineItem from) throws Exception {
		//TODO make this more extension-pointy
		if (from.value instanceof Map<?, ?>) {
			Map<Object, Object> map = (Map<Object, Object>) from.value;
			Object resourceType = map.get(CardConstants.slingResourceType);
			if (CardConstants.group.equals(resourceType))
				return from.key;
		}
		String key = findKey(from);
		String prettyKey = Strings.camelCaseToPretty(from.key);
		String fullKey = MessageFormat.format(namePattern, key );
		String pattern = IResourceGetter.Utils.get(resourceGetterFn, from.cardType, fullKey);
		if (pattern == null)
			return prettyKey;
		else
			return MessageFormat.format(pattern, key, prettyKey);
	}
}