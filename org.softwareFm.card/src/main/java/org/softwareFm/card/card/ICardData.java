package org.softwareFm.card.card;

import java.util.Map;

public interface ICardData extends IHasCardConfig {

	/** Typically this is determined by the sling:resourceType in the rawdata. May be null */
	String cardType();

	/** This is called when more information has been found about the card. The newValue should replace any existing data about this key. The key should already be present in the data */
	void valueChanged(String key, Object newValue);

	/** What url on the server does this card represent? */
	String url();

	/** A copy of the data that the card is displaying: thread safe, and changes to this map have no impact */
	Map<String, Object> data();
	

}
