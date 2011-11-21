package org.softwareFm.card.card;

/** Called when the card set in the cardholder */
public interface ICardChangedListener extends ICardValueChangedListener {

	/** Called when the card set in the cardholder */
	void cardChanged(ICardHolder cardHolder, ICard card);
	
	

}
