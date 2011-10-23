package org.softwareFm.card.internal.details;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardSelectedListener;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.card.internal.CardCollectionHolder;
import org.softwareFm.display.composites.IHasControl;

public class CollectionsDetailAdder implements IDetailAdder {
	@Override
	public IHasControl add(Composite parentComposite, ICard parentCard,
			CardConfig cardConfig, KeyValue keyValue,
			ICardSelectedListener listener) {
		if (keyValue.value instanceof List<?>)
			for (KeyValue kv : (List<KeyValue>) keyValue.value)
				if (kv.key.equals("sling:resourceType"))
					if (kv.value.equals("collection")) {
						CardCollectionHolder result = new CardCollectionHolder(
								parentComposite, cardConfig);
						result.setKeyValue(parentCard.url()+"/"+keyValue.key, keyValue);
						return result;
					}
		return null;
	}

}
