/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.navigation.internal;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.navigation.ITitleBarForCard;
import org.softwareFm.card.title.Title;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.utilities.functions.Functions;

public class NavTitle extends Title implements ITitleBarForCard {

	public NavTitle(Composite parent, final CardConfig cardConfig, TitleSpec titleSpec, String initialLabel, String tooltip) {
		super(parent, cardConfig, titleSpec, initialLabel, tooltip);
	}

	@Override
	public void setUrl(ICard card) {
		String title = (Functions.call(card.getCardConfig().cardTitleFn, card));
		TitleSpec titleSpec = Functions.call(card.getCardConfig().titleSpecFn, card);
		setTitleAndImage(title, card.url(), titleSpec);
	}


}