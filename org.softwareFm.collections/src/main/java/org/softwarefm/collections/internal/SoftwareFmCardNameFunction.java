package org.softwarefm.collections.internal;

import java.util.Map;

import org.softwareFm.card.card.AbstractLineItemFunction;
import org.softwareFm.card.card.LineItem;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.utilities.functions.IFunction1;

public class SoftwareFmCardNameFunction extends AbstractLineItemFunction<String> {

	private final IFunction1<LineItem, String> delegate;

	public SoftwareFmCardNameFunction(IFunction1<LineItem, String> delegate) {
		this.delegate = delegate;
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
		return delegate.apply(from);
	}
}