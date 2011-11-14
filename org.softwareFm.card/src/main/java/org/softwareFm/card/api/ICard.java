package org.softwareFm.card.api;

import java.util.Map;

import org.softwareFm.display.composites.IHasComposite;

public interface ICard extends IHasComposite, IHasTable {

	void valueChanged(String key, Object newValue);

	/** A copy of the data that the card is displaying: thread safe, and changes to this map have no impact */
	Map<String, Object> data();

	void addLineSelectedListener(ILineSelectedListener listener);
	
	void addValueChangedListener(ICardValueChangedListener listener);

	CardConfig cardConfig();

	String cardType();

	String url();

}
