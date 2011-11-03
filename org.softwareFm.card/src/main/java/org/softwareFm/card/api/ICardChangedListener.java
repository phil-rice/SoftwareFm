package org.softwareFm.card.api;

public interface ICardChangedListener extends ICardValueChangedListener {

	/** Called when the card set in the cardholder */
	void cardChanged(ICardHolder cardHolder, ICard card);
	
	

}
