package org.softwareFm.card.navigation;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.internal.details.TitleSpec;
import org.softwareFm.card.internal.title.Title;
import org.softwareFm.utilities.functions.Functions;

public class NavTitle extends Title implements ITitleBarForCard {

	public NavTitle(Composite parent, final CardConfig cardConfig, TitleSpec titleSpec, String initialLabel, String tooltip) {
		super(parent, cardConfig, titleSpec, initialLabel, tooltip);
	}

	@Override
	public void setUrl(ICard card) {
		String title = (Functions.call(card.cardConfig().cardTitleFn, card.url()));
		TitleSpec titleSpec = Functions.call(card.cardConfig().titleSpecFn, card);
		setTitleAndImage(title, card.url(), titleSpec);
	}


}
