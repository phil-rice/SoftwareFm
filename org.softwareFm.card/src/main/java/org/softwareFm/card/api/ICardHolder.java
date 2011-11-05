package org.softwareFm.card.api;

import org.softwareFm.display.composites.IHasComposite;

public interface ICardHolder extends IHasComposite {
	void setCard(ICard card);

	void setAddItemProcessor(IAddItemProcessor processor);

	void makeAndSetTableMenu(ICard card);

}
