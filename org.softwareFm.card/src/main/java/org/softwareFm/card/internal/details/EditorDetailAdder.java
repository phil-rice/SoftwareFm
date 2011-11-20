package org.softwareFm.card.internal.details;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.IDetailsFactoryCallback;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.resources.IResourceGetter;

public class EditorDetailAdder implements IDetailAdder{

	@Override
	public IHasControl add(Composite parentComposite, ICard parentCard, CardConfig cardConfig, String key, Object value, IDetailsFactoryCallback callback) {
		if (value instanceof String) {
			String editorName = IResourceGetter.Utils.getOr(cardConfig.resourceGetterFn, parentCard.cardType(), "editor." + key, "text");
			IDetailAdder editorAdder = Functions.call(cardConfig.editorFn, editorName);
			if (editorAdder != null)
				return editorAdder.add(parentComposite, parentCard, cardConfig, key, value, callback);
		}
		return null;
	}

}
