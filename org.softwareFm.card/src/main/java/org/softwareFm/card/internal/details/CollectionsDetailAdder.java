package org.softwareFm.card.internal.details;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.IDetailsFactoryCallback;
import org.softwareFm.card.internal.CardCollectionHolder;
import org.softwareFm.display.composites.IHasControl;

public class CollectionsDetailAdder implements IDetailAdder {
	@SuppressWarnings("unchecked")
	@Override
	public IHasControl add(Composite parentComposite, ICard parentCard, CardConfig cardConfig, String key, Object value, IDetailsFactoryCallback callback) {
		if (value instanceof Map<?, ?>) {
			Map<String, Object> map = (Map<String, Object>) value;
			Object type = map.get("sling:resourceType");
			if ("collection".equals(type)) {
				CardCollectionHolder result = new CardCollectionHolder(parentComposite, cardConfig);
				String newUrl = parentCard.url() +"/" + key;
				result.setKeyValue(newUrl, key, value, callback);
				return result;
			}
		}
		return null;
	}
}
