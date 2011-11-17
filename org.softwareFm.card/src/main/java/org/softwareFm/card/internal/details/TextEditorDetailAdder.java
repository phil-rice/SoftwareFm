package org.softwareFm.card.internal.details;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.IDetailsFactoryCallback;
import org.softwareFm.card.editors.TextEditor;
import org.softwareFm.card.editors.ValueEditorLayout;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.utilities.functions.Functions;

public class TextEditorDetailAdder implements IDetailAdder {

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
