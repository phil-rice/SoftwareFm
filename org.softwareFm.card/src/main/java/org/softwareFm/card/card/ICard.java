/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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