package org.softwareFm.card.api;

import org.softwareFm.display.composites.IHasControl;

public interface ICard extends IHasControl {

	void addLineSelectedListener(ILineSelectedListener listener);

}
