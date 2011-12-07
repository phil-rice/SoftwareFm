/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card;

import org.eclipse.swt.widgets.Listener;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.composites.IHasTable;

/** Represents a card being displayed on the screen. A card represents data about a node and it's child nodes on the server. The card starts with raw data (a JSON representation of the data on the server) that is enriched as more data is found */
public interface ICard extends IHasComposite, IHasTable, ICardData {



	/** Add a listener for the user clicking on a line in the card */
	void addLineSelectedListener(ILineSelectedListener listener);

	/** Add a listener for the user clicking on the card */
	void addValueChangedListener(ICardValueChangedListener listener);

	/** Add a menu detect listener to the table */
	void addMenuDetectListener(Listener listener);

}