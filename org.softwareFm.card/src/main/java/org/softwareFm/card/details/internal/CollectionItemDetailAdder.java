package org.softwareFm.card.details.internal;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.internal.CardConfigFillWithAspectRatioLayout;
import org.softwareFm.card.card.internal.OneCardHolder;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.details.IDetailAdder;
import org.softwareFm.card.details.IDetailsFactoryCallback;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts;

/** Treats as an item in a collection if it has a type, and isn't a collection itself */
public class CollectionItemDetailAdder implements IDetailAdder {

	@Override
	public IHasControl add(Composite parentComposite, ICard parentCard, CardConfig cardConfig, String key, Object value, IDetailsFactoryCallback callback) {
		if (value instanceof Map<?, ?>) {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) value;
			Object object = map.get(CardConstants.slingResourceType);
			if (object != null && !CardConstants.collection.equals(object)) {// it
				String url = parentCard.url() + "/" + key;
				OneCardHolder result = new OneCardHolder(parentComposite, cardConfig, url, key, callback);
				result.getComposite().setLayout(new CardConfigFillWithAspectRatioLayout());
				Swts.Size.setSizeFromClientArea(result.getControl());
				return result;
			}
		}
		return null;
	}

}
