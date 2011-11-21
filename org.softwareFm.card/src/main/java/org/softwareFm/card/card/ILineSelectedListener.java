package org.softwareFm.card.card;

/** The user has clicked on a line on the card */
public interface ILineSelectedListener {

	/** The user has clicked on a line on the card */
	void selected(ICard card, String key, Object value);

}
