/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.composites.IHasTable;
import org.softwareFm.swt.configuration.CardConfig;

/** Represents a card being displayed on the screen. A card represents data about a node and it's child nodes on the server. The card starts with raw data (a JSON representation of the data on the server) that is enriched as more data is found */
public interface ICard extends IHasComposite, IHasTable, ICardData {

	/** Add a listener for the user clicking on a line in the card */
	void addLineSelectedListener(ILineSelectedListener listener);

	/** Add a listener for the user clicking on the card */
	void addValueChangedListener(ICardValueChangedListener listener);

	/** Add a menu detect listener to the table */
	void addMenuDetectListener(Listener listener);

	static class Utils {
		public static void setCardTableColumnWidths(Table table, CardConfig cardConfig) {
			Point size = table.getSize();
			TableColumn nameColumn = table.getColumn(0);
			TableColumn valueColumn = table.getColumn(1);
			nameColumn.pack();
			int idealNameWidth = nameColumn.getWidth();
			int newNameWidth = (size.x * cardConfig.cardNameWeight) / (cardConfig.cardNameWeight + cardConfig.cardValueWeight);
			int maxNameValue = (int) (idealNameWidth * cardConfig.cardMaxNameSizeRatio);
			if (newNameWidth > maxNameValue)
				newNameWidth = maxNameValue;
			int newValueWidth = size.x - newNameWidth - 1;
			nameColumn.setWidth(newNameWidth);
			valueColumn.setWidth(newValueWidth);
		}

	}

}