package org.softwareFm.card.card;

import java.util.Map;

import org.eclipse.swt.widgets.Listener;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.composites.IHasTable;

/** Represents a card being displayed on the screen. A card represents data about a node and it's child nodes on the server. The card starts with raw data (a JSON representation of the data on the server) that is enriched as more data is found */
public interface ICard extends IHasComposite, IHasTable {

	/** This is called when more information has been found about the card. The newValue should replace any existing data about this key. The key should already be present in the data */
	void valueChanged(String key, Object newValue);

	/** A copy of the data that the card is displaying: thread safe, and changes to this map have no impact */
	Map<String, Object> data();

	/** Add a listener for the user clicking on a line in the card */
	void addLineSelectedListener(ILineSelectedListener listener);

	/** Add a listener for the user clicking on the card */
	void addValueChangedListener(ICardValueChangedListener listener);

	/** Add a menu detect listener to the table */
	void addMenuDetectListener(Listener listener);

	/** The card config for this card */
	CardConfig cardConfig();

	/** Typically this is determined by the sling:resourceType in the rawdata. May be null */
	String cardType();

	/** What url on the server does this card represent? */
	String url();
	

}
