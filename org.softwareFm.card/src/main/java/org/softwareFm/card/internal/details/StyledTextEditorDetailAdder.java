package org.softwareFm.card.internal.details;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.internal.editors.StyledTextEditor;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class StyledTextEditorDetailAdder implements IDetailAdder {

	@Override
	public IHasControl add(Composite parentComposite, ICard parentCard, CardConfig cardConfig, String key, Object value, IDetailsFactoryCallback callback) {
		if (value instanceof String) {
			List<String> needingStyled = Strings.splitIgnoreBlanks(IResourceGetter.Utils.get(parentCard.cardConfig().resourceGetter, CardConstants.editorStyledText), ",");
			if (needingStyled.contains(key)) {
				TitleSpec titleSpec = Functions.call(cardConfig.titleSpecFn, parentCard);
				StyledTextEditor result = new StyledTextEditor(parentComposite, cardConfig, parentCard.url(), key, value, callback, titleSpec);
				callback.gotData(result.getControl());
				return result;
			}
		}
		return null;
	}

}
