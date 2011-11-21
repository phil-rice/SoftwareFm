package org.softwareFm.card.details.internal;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.CardConfig;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.internal.ListOfCards;
import org.softwareFm.card.card.internal.ScrollingCardCollectionHolder;
import org.softwareFm.card.details.IDetailAdder;
import org.softwareFm.card.details.IDetailsFactoryCallback;
import org.softwareFm.display.composites.IHasControl;

public class CollectionsDetailAdder implements IDetailAdder {
	@SuppressWarnings("unchecked")
	@Override
	public IHasControl add(Composite parentComposite, ICard parentCard, CardConfig cardConfig, String key, Object value, IDetailsFactoryCallback callback) {
		if (value instanceof Map<?, ?>) {
			Map<String, Object> map = (Map<String, Object>) value;
			Object type = map.get("sling:resourceType");
			if ("collection".equals(type)) {
				if (useListOfCards) {
					ListOfCards result = new ListOfCards(parentComposite, cardConfig);
					result.setKeyValue(parentCard.url() + "/" + key, "", key, value, callback);
					return result;
				} else {
					ScrollingCardCollectionHolder result = new ScrollingCardCollectionHolder(parentComposite, cardConfig);
					String newUrl = parentCard.url() + "/" + key;
					result.setKeyValue(newUrl, key, value, callback);
					return result;
				}
			}
		}
		return null;
	}
}