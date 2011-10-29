package org.softwareFm.card.internal.details;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardSelectedListener;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.card.internal.CardCollectionHolder;
import org.softwareFm.display.composites.IHasControl;

public class CollectionsDetailAdder implements IDetailAdder {
	@SuppressWarnings("unchecked")
	@Override
	public IHasControl add(Composite parentComposite, ICard parentCard, CardConfig cardConfig, KeyValue keyValue, ICardSelectedListener listener, Runnable afterEdit) {
		if (keyValue.value instanceof List<?>)
			for (KeyValue kv : (List<KeyValue>) keyValue.value)
				if (kv.value instanceof Map<?, ?>) {
					Object object = ((Map<String, Object>) kv.value).get("sling:resourceType");
					if ("collection".equals(object)) {
						CardCollectionHolder result = new CardCollectionHolder(parentComposite, cardConfig);
						String newUrl = parentCard.url();
						result.setKeyValue(newUrl, keyValue);
						result.addCardSelectedListener(listener);
						return result;
					}
				}
		return null;
	}

}
