package org.softwareFm.card.navigation;

import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.internal.components.Title;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;

public class NavTitle extends Title implements ITitleBarForCard {

	public NavTitle(Composite parent, final CardConfig cardConfig, String initialLabel, String tooltip) {
		super(parent, cardConfig, initialLabel, tooltip);
	}

	@Override
	public void setUrl(ICard card) {
		IFunction1<Map<String, Object>, Image> cardIconFn = card.cardConfig().cardIconFn;
		Image image = Functions.call(cardIconFn, card.data());
		String title = (Functions.call(card.cardConfig().cardTitleFn, card.url()));
		setTitleAndImage(title, image);
	}

}
