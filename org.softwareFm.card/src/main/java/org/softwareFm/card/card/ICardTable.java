/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.softwareFm.card.card.internal.CardTable;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.composites.IHasControl;

public interface ICardTable extends IHasControl {

	Table getTable();

	public static class Utils {
		public static ICardTable cardTable(Composite parent, CardConfig cardConfig, TitleSpec titleSpec, String cardType, Map<String, Object> data) {
			return new CardTable(parent, cardConfig, titleSpec, cardType, data);
		}
	}

}