package org.softwarefm.collections.internal;

import java.util.Map;

import org.softwareFm.card.card.AbstractLineItemFunction;
import org.softwareFm.card.card.ILineItemFunction;
import org.softwareFm.card.card.LineItem;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.utilities.resources.IResourceGetter;

public class SoftwareFmCardNameFunction extends AbstractLineItemFunction<String> {

	private final ILineItemFunction<String> delegate;

	public SoftwareFmCardNameFunction(ILineItemFunction<String> delegate) {
		this.delegate = delegate;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String apply(CardConfig cardConfig, LineItem from) {
		if (from.value instanceof Map<?, ?>) {
			Map<Object, Object> map = (Map<Object, Object>) from.value;
			String resourceType = (String) map.get(CardConstants.slingResourceType);
			String cardNameKey = IResourceGetter.Utils.getOrNull(cardConfig.resourceGetterFn, resourceType, CardConstants.cardNameFieldKey);
			String name = (String) map.get(cardNameKey);
			if (name != null)
				return name;
		}
		return delegate.apply(cardConfig, from);
	}
}