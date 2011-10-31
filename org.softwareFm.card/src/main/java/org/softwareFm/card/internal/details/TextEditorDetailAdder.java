package org.softwareFm.card.internal.details;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardSelectedListener;
import org.softwareFm.card.internal.editors.TextEditor;
import org.softwareFm.display.composites.IHasControl;

public class TextEditorDetailAdder implements IDetailAdder {

	@Override
	public IHasControl add(Composite parentComposite, ICard parentCard, CardConfig cardConfig, String  key, Object value, ICardSelectedListener listener, Runnable afterEdit) {
		if (value instanceof String)
			return new TextEditor(parentComposite, parentCard,cardConfig, key, value, afterEdit);
		else
			return null;
	}

}
