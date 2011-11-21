package org.softwareFm.card.softwareFm.internal;

import java.util.Map;

import org.softwareFm.card.card.LineItem;
import org.softwareFm.card.card.internal.CardNameFunction;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

public class SoftwareFmCardNameFunction extends CardNameFunction {

	public SoftwareFmCardNameFunction(IFunction1<String, IResourceGetter> resourceGetterFn, String namePattern) {
		super(resourceGetterFn, namePattern);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String apply(LineItem from) throws Exception {
		if (from.value instanceof Map<?, ?>) {
			Map<Object, Object> map = (Map<Object, Object>) from.value;
			Object resourceType = map.get(CardConstants.slingResourceType);
			if (CardConstants.group.equals(resourceType))
				return from.key;
		}
		return super.apply(from);
	}
}