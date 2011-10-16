package org.softwareFm.card.api;

import org.softwareFm.display.composites.IHasComposite;

public interface ICard extends IHasComposite {

	void addLineSelectedListener(ILineSelectedListener listener);

	String url();

}
