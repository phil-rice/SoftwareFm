/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.internal;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swt.card.IHasCardConfig;
import org.softwareFm.swt.configuration.CardConfig;

public class CardConfigFillWithAspectRatioLayout extends FillWithAspectRatioLayout {

	@Override
	protected int getWidthWeight(Composite composite) {
		return getCardConfig(composite).widthWeight;
	}

	@Override
	protected int getHeightWeight(Composite composite) {
		return getCardConfig(composite).heightWeight;
	}

	private CardConfig getCardConfig(Composite composite) {
		IHasCardConfig hasCardConfig = (IHasCardConfig) composite;
		CardConfig cardConfig = hasCardConfig.getCardConfig();
		return cardConfig;
	}

}