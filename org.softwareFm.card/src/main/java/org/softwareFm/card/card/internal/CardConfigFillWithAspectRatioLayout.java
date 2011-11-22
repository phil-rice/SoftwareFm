package org.softwareFm.card.card.internal;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.IHasCardConfig;
import org.softwareFm.card.configuration.CardConfig;

public class CardConfigFillWithAspectRatioLayout extends FillWithAspectRatioLayout {

	@Override
	protected int getWidthWeight(Composite composite) {
		return getCardConfig(composite).widthWeight;
	}

	@Override
	protected int getHeightWeight(Composite composite) {
		return getCardConfig(composite).heightWeight;
	}

	private CardConfig getCardConfig(Composite composite) {
		IHasCardConfig hasCardConfig = (IHasCardConfig) composite;
		CardConfig cardConfig = hasCardConfig.getCardConfig();
		return cardConfig;
	}

}
