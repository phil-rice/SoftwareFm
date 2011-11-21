package org.softwareFm.card.api;

import org.softwareFm.display.composites.IHasComposite;

/** The card holder includes the title for the card. Typically it is displayed before the card data has been found */
public interface ICardHolder extends IHasComposite {
	void setCard(ICard card);


}
