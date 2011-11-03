package org.softwareFm.card.api;

public interface ICardValueChangedListener {
	/** Called when the card is updated */
	void valueChanged(ICard card, String key, Object newValue);
}
