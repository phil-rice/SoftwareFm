package org.softwareFm.card.navigation.internal;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.CardConfig;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.navigation.ITitleBarForCard;
import org.softwareFm.card.title.Title;
import org.softwareFm.card.title.TitleSpec;
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
