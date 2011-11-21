package org.softwareFm.card.navigation;

import org.softwareFm.card.card.ICard;
import org.softwareFm.display.composites.IHasControl;

public interface ITitleBarForCard extends IHasControl {


	void setUrl(ICard card);
}
