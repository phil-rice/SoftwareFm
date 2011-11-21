package org.softwareFm.card.card;

/** Called when the card is updated */
public interface ICardValueChangedListener {
	/** Called when the card is updated */
	void valueChanged(ICard card, String key, Object newValue);
}
