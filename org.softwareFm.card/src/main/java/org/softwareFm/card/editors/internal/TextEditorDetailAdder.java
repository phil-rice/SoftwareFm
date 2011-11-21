package org.softwareFm.card.editors.internal;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.details.IDetailsFactoryCallback;
import org.softwareFm.card.editors.IEditorDetailAdder;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.utilities.functions.Functions;

public class TextEditorDetailAdder implements IEditorDetailAdder {

	@Override
	public IHasControl add(Composite parentComposite, ICard parentCard, CardConfig cardConfig, String key, Object value, IDetailsFactoryCallback callback) {
		if (value instanceof String) {
			TitleSpec titleSpec = Functions.call(cardConfig.titleSpecFn, parentCard);
			TextEditor result = new TextEditor(parentComposite, cardConfig, parentCard.url(),parentCard.cardType(),  key, value, callback, titleSpec);
			result.getComposite().setLayout(new ValueEditorLayout());
			callback.gotData(result.getControl());
			return result;
		} else
			return null;
	}

}
